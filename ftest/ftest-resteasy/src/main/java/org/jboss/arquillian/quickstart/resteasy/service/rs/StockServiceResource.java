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
package org.jboss.arquillian.quickstart.resteasy.service.rs;

import org.jboss.arquillian.quickstart.resteasy.model.Stock;
import org.jboss.arquillian.quickstart.resteasy.service.StockService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class StockServiceResource implements StockService {

    private static final int MAX_RESULTS = 5;

    private static final Map<Long, Stock> stockMap = new ConcurrentHashMap<Long, Stock>();

    private static final AtomicLong counter = new AtomicLong(1L);

    /**
     * Creates new instance of {@link StockServiceResource} class.
     */
    public StockServiceResource() {

        // creates test stock
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setName("Acme");
        stock.setCode("ACM");
        stock.setValue(new BigDecimal(37.5D));
        stock.setDate(new Date());

        stockMap.put(stock.getId(), stock);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response createStock(Stock stock) {

        // assigns the stock id
        stock.setId(counter.incrementAndGet());
        // saves the stock
        stockMap.put(stock.getId(), stock);
        // creates response
        return Response.created(URI.create("/stocks/" + stock.getId())).build();
    }

    @Override
    public void updateStock(@PathParam("id") long id, Stock stock) {

        Stock current = stockMap.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // updates the stock
        current.setName(stock.getName());
        current.setCode(stock.getCode());
        current.setDate(stock.getDate());
        current.setValue(stock.getValue());
    }

    @Override
    public Stock getStock(@PathParam("id") long id) {

        Stock stock = stockMap.get(id);
        if (stock == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return stock;
    }

    @Override
    public List<Stock> getStocks(@DefaultValue("0")int startIndex, @DefaultValue("10")int size)  {

        // gets the list of all stocks in the current map
        List<Stock> stocks = new ArrayList<Stock>(MAX_RESULTS);

        Iterator<Stock> iter = stockMap.values().iterator();
        int count = 0;

        // skips records
        while(iter.hasNext() && count < startIndex) {
            iter.next();
        }

        while (iter.hasNext() && count < MAX_RESULTS) {
            stocks.add(iter.next());
        }

        return stocks;
    }

    @Override
    public Response deleteStock(@PathParam("id") long id) {

        if (stockMap.remove(id) == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return Response.ok().build();
    }
}
