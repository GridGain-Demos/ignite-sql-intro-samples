DROP TABLE IF EXISTS Employer;

CREATE TABLE Employer (
  ID INT(11),
  Name CHAR(35),
  Salary DOUBLE(11),
  PRIMARY KEY (ID)
) WITH "template=partitioned, CACHE_NAME=Employer";

INSERT INTO Employer(ID, Name, Salary) VALUES (1,'Igor',10);
INSERT INTO Employer(ID, Name, Salary) VALUES (2,'Roman',15);
INSERT INTO Employer(ID, Name, Salary) VALUES (3,'Nikolay',20);
