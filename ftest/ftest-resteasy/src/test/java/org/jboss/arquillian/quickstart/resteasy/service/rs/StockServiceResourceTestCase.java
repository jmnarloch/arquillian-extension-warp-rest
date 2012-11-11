/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.quickstart.resteasy.service.rs;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.quickstart.resteasy.application.StockApplication;
import org.jboss.arquillian.quickstart.resteasy.model.Stock;
import org.jboss.arquillian.quickstart.resteasy.service.StockService;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(Arquillian.class)
public class StockServiceResourceTestCase {

    /**
     * Creates the test deployment.
     *
     * @return the test deployment
     */
    @Deployment
    @OverProtocol("Servlet 3.0")
    public static Archive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(StockApplication.class, Stock.class, StockService.class, StockServiceResource.class)
                .addAsWebInfResource("web.xml");
    }

    /**
     * The context path of the deployed application.
     */
    @ArquillianResource
    private URL contextPath;

    /**
     * The service client.
     */
    private StockService stockService;

    /**
     * Sets up the test environment.
     */
    @BeforeClass
    public static void setUpClass() {

        // initializes the rest easy client framework
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    /**
     * Sets up class.
     */
    @Before
    public void setUp() {

        stockService = ProxyFactory.create(StockService.class, contextPath.toString());
    }

    @Test
    @RunAsClient
    public void testStockCreate() {

        // creates the stock
        Response response = stockService.createStock(createStock());

        assertEquals("The request didn't succeeded.", Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    public void testStockGet() {

        Stock stock = createStock();
        ClientResponse response = (ClientResponse)stockService.createStock(stock);
        response.releaseConnection();

        Stock result = stockService.getStock(1L);

        assertEquals("Stock has invalid name.", stock.getName(), result.getName());
        assertEquals("Stock has invalid code.", stock.getCode(), result.getCode());
        assertEquals("Stock has invalid value.", stock.getValue(), result.getValue());
    }

    @Test(expected = ClientResponseFailure.class)
    @RunAsClient
    public void testStockGetFailure() {

        stockService.getStock(0L);
    }

    /**
     * Creates the instance of {@link Stock} for testing
     *
     * @return the created stock instance
     */
    private Stock createStock() {

        Stock stock = new Stock();
        stock.setName("Acme");
        stock.setCode("ACM");
        stock.setValue(new BigDecimal(127D));
        stock.setDate(new Date());
        return stock;
    }
}