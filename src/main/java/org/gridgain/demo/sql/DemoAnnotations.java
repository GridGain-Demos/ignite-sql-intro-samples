/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gridgain.demo.sql;

import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.gridgain.demo.sql.model.City;

/**
 * The demo shows how to define SQL configuration programmatically with annotations, specify such indexed types and query
 * records with the usage of "_key" keyword that refers to the primary key in Ignite.
 */
public class DemoAnnotations {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        IgniteConfiguration cfg = new IgniteConfiguration();

        //Preparing the network configuration.
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        //Starting a server node instance.
        Ignite serverNode = Ignition.start(cfg);

        //Preparing a cache configuration.
        CacheConfiguration cityCacheCfg = new CacheConfiguration("CityCache");

        //Passing information about queryable fields and indexes.
        cityCacheCfg.setIndexedTypes(Integer.class, City.class);

        //Changing schema to 'PUBLIC'. Otherwise, the cache name is used as a schema name.
        cityCacheCfg.setSqlSchema("PUBLIC");

        //Staring cache dynamicly.
        IgniteCache<Integer, City> cityCache = serverNode.getOrCreateCache(cityCacheCfg);

        //Putting a cache value.
        cityCache.put(474, new City("Wolverhampton","GBR","England",
            242000));

        //Selecting the record using "_key" keyword for the primary key.
        Iterable resultSet = cityCache.query(new SqlFieldsQuery("SELECT * FROM City WHERE _key = 474")).getAll();

        System.out.println(">> Returned value:");

        for (Object col: resultSet)
            System.out.println(col);

        serverNode.close();
    }

}
