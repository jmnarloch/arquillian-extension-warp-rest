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
package org.jboss.arquillian.warp.extension.rest.resteasy.integration;

import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.WarpRestCommons;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 *
 */
@Provider
@ServerInterceptor
public class WarpResteasyInterceptor implements PreProcessInterceptor, PostProcessInterceptor,
        MessageBodyReaderInterceptor, MessageBodyWriterInterceptor {

    /**
     * Stores the  for the current thread.
     */
    private static final ThreadLocal<HttpRequest> request = new ThreadLocal<HttpRequest>();

    /**
     * Stores the context builder for the current thread.
     */
    private static final ThreadLocal<ResteasyContextBuilder> builder = new ThreadLocal<ResteasyContextBuilder>();

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethod resourceMethod) throws Failure, WebApplicationException {

        // stores the http request
        request.set(httpRequest);

        // stores the execution context
        if(builder.get() == null) {
            builder.set(new ResteasyContextBuilder());
        }
        builder.set(new ResteasyContextBuilder());
        builder.get().setHttpRequest(httpRequest);

        // saves the the rest context in the request
        storeRestContext();

        // returns null, does not overrides the original server response
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException {

        // reads the entity from the request
        Object result = context.proceed();

        if(builder.get() == null) {
            builder.set(new ResteasyContextBuilder());
        }
        builder.get().setRequestEntity(result);

        // saves the creates context in the request
        storeRestContext();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcess(ServerResponse serverResponse) {

        // sets the server response
        if(builder.get() == null) {
            builder.set(new ResteasyContextBuilder());
        }
        builder.get().setServerResponse(serverResponse);

        // saves the the rest context in the request
        storeRestContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {

        context.proceed();
        if(builder.get() == null) {
            builder.set(new ResteasyContextBuilder());
        }
        builder.get().setResponseMediaType(context.getMediaType());

        // saves the the rest context in the request
        storeRestContext();
    }

    /**
     * Stores the rest context in the request as an attribute.
     */
    private void storeRestContext() {

        RestContext restContext = builder.get().build();

        request.get().setAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE, restContext);
    }
}
