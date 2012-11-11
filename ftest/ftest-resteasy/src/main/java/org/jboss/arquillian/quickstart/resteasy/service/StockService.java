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
package org.jboss.arquillian.quickstart.resteasy.service;

import org.jboss.arquillian.quickstart.resteasy.model.Stock;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/stocks")
public interface StockService {

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    Response createStock(Stock stock);

    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    void updateStock(@PathParam("id") long id, Stock stock);

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    Stock getStock(@PathParam("id") long id);

    @Path("/{id}")
    @DELETE
    Response deleteStock(@PathParam("id") long id);
}
