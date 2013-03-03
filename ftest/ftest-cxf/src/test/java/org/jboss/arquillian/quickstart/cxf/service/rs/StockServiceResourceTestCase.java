/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.quickstart.cxf.service.rs;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.quickstart.cxf.application.StockApplication;
import org.jboss.arquillian.quickstart.cxf.model.Stock;
import org.jboss.arquillian.quickstart.cxf.service.StockService;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.extension.rest.api.HttpMethod;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import static org.jboss.arquillian.warp.client.filter.http.HttpFilters.request;
import static org.junit.Assert.assertEquals;

/**
 *
 */
@WarpTest
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

        File[] libs = DependencyResolvers.use(MavenDependencyResolver.class)
                .loadMetadataFromPom("pom.xml")
                .artifacts("org.easytesting:fest-assert")
                .artifacts("org.apache.cxf:cxf-rt-frontend-jaxrs")
                .artifacts("org.apache.cxf:cxf-rt-rs-extension-providers")
                .artifacts("org.apache.cxf:cxf-rt-transports-http")
                .artifacts("org.apache.cxf:cxf-rt-rs-extension-search")
                .artifacts("org.codehaus.jettison:jettison")
                .artifacts("org.springframework:spring-web")
                .resolveAsFiles();

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(StockApplication.class, Stock.class, StockService.class, StockServiceResource.class)
                .addAsWebInfResource("WEB-INF/web.xml")
                .addAsWebInfResource("WEB-INF/cxf.xml")
                .addAsLibraries(libs);
    }

    /**
     * The context path of the deployed application.
     */
    @ArquillianResource
    private URL contextPath;

    /**
     * Represents the REST service client.
     */
    private StockService stockService;

    /**
     * Represents the REST service client.
     */
    private WebClient client;

    /**
     * <p>Sets up the test environment.</p>
     */
    @Before
    public void setUp() {

        JSONProvider provider = new JSONProvider();
        provider.setSerializeAsArray(true);

        stockService = JAXRSClientFactory.create(contextPath + "app/rest/",
                StockService.class, Arrays.asList(provider));

        WebClient.client(stockService)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    @RunAsClient
    public void testStockCreate() {

        Stock stock = createStock();

        Response response = stockService.createStock(stock);

        assertEquals("The request didn't succeeded.", Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    @RunAsClient
    public void testStockGet() {

        Stock stock = createStock();
        stockService.createStock(stock);

        Stock result = stockService.getStock(2L);

        assertEquals("Stock has invalid name.", stock.getName(), result.getName());
        assertEquals("Stock has invalid code.", stock.getCode(), result.getCode());
        assertEquals("Stock has invalid value.", stock.getValue(), result.getValue());
    }

    @Test
    @RunAsClient
    public void testStockGetWarp() {

        final Stock stock = createStock();

        stockService.createStock(stock);

        Warp.initiate(new Activity() {
            @Override
            public void perform() {

                Stock result = stockService.getStock(2l);

                assertEquals("Stock has invalid name.", stock.getName(), result.getName());
                assertEquals("Stock has invalid code.", stock.getCode(), result.getCode());
                assertEquals("Stock has invalid value.", stock.getValue(), result.getValue());
            }
        }).observe(request().uri().contains("rest/stocks/2")).inspect(new Inspection() {

            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext;

            @AfterServlet
            public void testGetStock() {

                assertEquals(HttpMethod.GET, restContext.getHttpRequest().getMethod());
                assertEquals(200, restContext.getHttpResponse().getStatusCode());
                assertEquals("text/xml", restContext.getHttpResponse().getContentType());
            }
        });
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
