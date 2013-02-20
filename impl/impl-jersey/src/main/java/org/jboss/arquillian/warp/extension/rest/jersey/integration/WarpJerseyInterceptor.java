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
package org.jboss.arquillian.warp.extension.rest.jersey.integration;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.WarpRestCommons;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.Map;

/**
 *
 */
@Provider
public class WarpJerseyInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Injected {@link HttpServletRequest}.
     */
    @Context
    private HttpServletRequest request;

    /**
     * Stores the context builder for the current thread.
     */
    private static final ThreadLocal<JerseyContextBuilder> builder = new ThreadLocal<JerseyContextBuilder>();

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {

        if(builder.get() == null) {
            builder.set(new JerseyContextBuilder());
        }
        builder.get().setContainerRequest(containerRequest);

        storeRestContext();

        return containerRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {

        if(builder.get() == null) {
            builder.set(new JerseyContextBuilder());
        }
        builder.get().setContainerResponse(containerResponse);
        storeRestContext();

        return containerResponse;
    }

    /**
     * Stores the rest context in the request as an attribute.
     */
    private void storeRestContext() {

        RestContext restContext = builder.get().build();

        request.setAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE, restContext);
    }
}
