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
package org.jboss.arquillian.warp.extension.rest.cxf.interceptor;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.ext.ResponseHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.WarpRestCommons;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 *
 */
public class WarpCxfInterceptor implements RequestHandler, ResponseHandler {

    // TODO non-thread safe?
    @Context
    private MessageContext messageContext;

    /**
     * Stores the context builder for the current thread.
     */
    private static final ThreadLocal<CxfContextBuilder> builder = new ThreadLocal<CxfContextBuilder>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Response handleRequest(Message message, ClassResourceInfo classResourceInfo) {

        builder.set(new CxfContextBuilder());
        builder.get().setRequestMessage(message);

        storeRestContext();

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response handleResponse(Message message, OperationResourceInfo operationResourceInfo, Response response) {

        builder.get().setResponseMessage(message).setResponse(response);

        storeRestContext();

        return null;
    }

    /**
     * Stores the rest context in the request as an attribute.
     */
    private void storeRestContext() {
        RestContext restContext = builder.get().build();

        messageContext.getHttpServletRequest().setAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE, restContext);
    }
}
