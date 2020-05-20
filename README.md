# Apache Ignite SQL Introductory Level Demos

This project includes some of the demos demonstrated during ["Getting Started With Apache Ignite SQL" webinar](
https://www.gridgain.com/resources/webinars/getting-started-apache-ignite-and-sql). Follow the steps below to play with 
the samples.  

## Demo #1 - Database Creation and Simple Querying

The demo shows how to start an Ignite cluster and create a World database using SQLLine tool.

* Download Ignite and start a 2-nodes cluster using `{ignite}/bin/ignite.[sh|bat]` script
* Connect to the cluster with SQLLine: `{ignite}/bin/sqlline.[sh|bat] --verbose=true -u jdbc:ignite:thin://127.0.0.1/`
* Create and fill in the World database by running this command from SQLLine: `!run {root_of_this_project}/scripts/ignite_world_no_collocation.sql`.
* Connect to the cluster with GridGain WebConsole (see [different installation options](https://www.gridgain.com/docs/web-console/latest/web-console-getting-started)).
* Execute a simple SQL query from WebConsole's SQL Notebooks screen: 
```
SELECT name, MAX(population) as max_pop FROM country
 GROUP BY name, population ORDER BY max_pop DESC LIMIT 3
```
 
## Demo #2 - Co-located and non-co-located SQL With JOINs 

The demo showcases how to run non-colocated and co-located joins with Ignite.

* Keep using the cluster started for Demo #1.
* Run the following SQL that joins two tables. You'll get a wrong result because `Country` and `City` tables are not co-located.
```
SELECT country.name, city.name, MAX(city.population) as max_pop FROM country
     JOIN city ON city.countrycode = country.code
     WHERE country.code IN ('USA','RUS','CHN','KOR','MEX','AUT','BRA','ESP','JPN')
     GROUP BY country.name, city.name 
     ORDER BY max_pop DESC LIMIT 3;
```
* Switch on `Allow colocated joins` flag with GridGain Web Console to allow data shuffling. This will enable so called
non-colocated joins and you'll see a correct result.     
* Open the terminal window with the SQLLine connection to the cluster and reload the  database using `!run {root_of_this_project}/scripts/ignite_world.sql`.
That script sets up affinity co-location between `Country` and `City` (check `affinityKey=CountryCode` parameter passed to `CREATE TABLE City` command).
* Run the same SQL query with join again (disable `Allow colocated joins flag`). You'll get a valid result and the query
will complete faster because records were not shuffled between the 2 cluster nodes during the JOIN phase of the query.

## Demo #3 - Running SQL over disk-only records

This demo shows that Ignite SQL engine can query in-memory as well as disk-only records. First, you'll create a cluster
that keeps 100% of records on disk and caches a subset in memory. A sample SQL query will be used to show that Ignite
queries in-memory and disk-tier transparently. Second, you'll restart the cluster and won't rehydrate memory with on-disk data.
Instead, you'll run the same query and Ignite will server all the data from disk. 

* Start a new 2-nodes cluster with `{root_of_this_project}/config/ignite-small-memory-region.xml` configuration.
* Connect to the cluster with SQLLine: `{ignite}/bin/sqlline.[sh|bat] --verbose=true -u jdbc:ignite:thin://127.0.0.1/`
* Run `!run {root_of_this_project}/scripts/fielding.sql` script.
* Run command:
```
COPY FROM '{root_of_this_project}/data/Fielding.csv' INTO Fielding (ID,playerID,yearID,stint,teamID,lgID,POS,G,GS,InnOuts,PO,A,E,DP,PB,WP,SB,CS,ZR) FORMAT CSV;
```
The loading will fail and one of the node will print out the following exception:
```
class org.apache.ignite.internal.mem.IgniteOutOfMemoryException: Out of memory in data region
```
* Restart the cluster (both nodes) with `{root_of_this_project}/config/ignite-small-memory-region-persistence-enabled.xml` 
configuration and activate it with `{ignite}/bin/control.sh --activate`.
* Reconnect to the cluster with SQLLine and try to reload the database again running `!run {root_of_this_project}/scripts/fielding.sql` script
and command:
```
COPY FROM '{root_of_this_project}/data/Fielding.csv' INTO Fielding (ID,playerID,yearID,stint,teamID,lgID,POS,G,GS,InnOuts,PO,A,E,DP,PB,WP,SB,CS,ZR) FORMAT CSV;
```

This time you'll succeed loading the database because 100% of data will be stored in Ignite native persistence while a subset will be cached in memory.
* Go to the Web Console Dashboard screen and check default region's memory metrics. You will see that the memory space is fully utilized 
while the disk usage is seven times more than available memory space.  
* Execute a simple SQL query from WebConsole's SQL Notebooks screen: 
```
SELECT * FROM Fielding ORDER BY yearID DESC
```
* Stop the cluster and bring the nodes back again.
* Check with WebConsole that the memory regions are empty (the data is available on disk only).
* Execute the same query, Ignite will serve data from disk and didn't lose a bit of data during the abrupt 
cluster termination.

## Demo #4 - Calcite Prototype Demo With Sub-Queries

This demo shows how Ignite's new SQL engine powered by Apache Calcite SQL can already execute requests with sub-queries.

* Build Ignite distribution from a feature branch running the following commands:
```
git clone --depth 1 --branch ignite-12248 https://gitbox.apache.org/repos/asf/ignite ignite-calcite
cd ./ignite-calcite
mvn clean package -DskipTests -Prelease,lgpl
cd ./target/release-package-apache-ignite
cp -r ./libs/optional/ignite-calcite ./libs/
cp -r ./libs/optional/ignite-slf4j ./libs/
``` 
* Start a 3-nodes Ignite cluster with the following configuration: `./bin/ignite.[sh|bat] ./config/default-config.xml`
* Connect to the cluster with SQLLine: `./bin/sqlline.[sh|bat] --verbose=true -u jdbc:ignite:thin://127.0.0.1/`
* Create tables using this script: `!run {root_of_this_project}/scripts/employer.sql`
* Execute a query:
```
SELECT * FROM Employer WHERE Salary = (SELECT AVG(Salary) FROM employer);
```
* There are only three records that you inserted using `employer.sql` script:
```
INSERT INTO Employer(ID, Name, Salary) VALUES (1,'Igor',10);
INSERT INTO Employer(ID, Name, Salary) VALUES (2,'Roman',15);
INSERT INTO Employer(ID, Name, Salary) VALUES (3,'Nikolay',20);
```
You expect getting `[2, "Roman", 15]` as a result. Check the actual result is incorrect.
* Close the SQLLine session: `!quit`
* Connect to the cluster enabling the new Calcite-powered engine: `./bin/sqlline.[sh|bat] --verbose=true -u 'jdbc:ignite:thin://127.0.0.1/?useExperimentalQueryEngine=true'`
* Execute the same query:
```
SELECT * FROM Employer WHERE Salary = (SELECT AVG(Salary) FROM employer);
```
* Check the result is correct now.

## Other Demos

Check `DemoAnnotations` and `DemoQueryEntities` for alternate ways of SQL configuration in Ignite.

