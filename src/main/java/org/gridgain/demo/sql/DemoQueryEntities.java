package org.gridgain.demo.sql;

import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.gridgain.demo.sql.model.City2;

/**
 * The demo shows how to define SQL configuration via SpringXML and QueryEntities, and query
 * records with the usage of "_key" keyword that refers to the primary key in Ignite.
 */
public class DemoQueryEntities {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        //Starting a server node instance.
        Ignite serverNode = Ignition.start("ignite-sql-intro/config/ignite-query-entity-config.xml");

        //Staring cache dynamicly.
        IgniteCache<Integer, City2> cityCache = serverNode.cache("CityCache2");

        //Putting a cache value.
        cityCache.put(474, new City2("Wolverhampton","GBR","England",
            242000));

        //Selecting the record using "_key" keyword for the primary key.
        Iterable resultSet = cityCache.query(new SqlFieldsQuery("SELECT * FROM City2 WHERE _key = 474")).getAll();

        System.out.println(">> Returned value:");

        for (Object col: resultSet)
            System.out.println(col);

        serverNode.close();
    }
}
