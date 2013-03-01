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
package org.jboss.arquillian.warp.extension.rest.cxf.interceptor;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.ext.ResponseHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.WarpRestCommons;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static org.jboss.arquillian.warp.extension.rest.cxf.interceptor.CxfContextBuilder.buildContext;

/**
 * CXF interceptor. This class implements {@link RequestHandler} and {@link ResponseHandler} in order to capture the execution state within
 * the server.
 * <p/>
 * Implementation captures the state and stores it the {@link RestContext} which is being bound to
 * executing request.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@Provider
public class WarpCxfInterceptor implements RequestHandler, ResponseHandler {

    /**
     * Represents the servlet request.
     */
    private static final ThreadLocal<HttpServletRequest> servletRequest =
            new InheritableThreadLocal<HttpServletRequest>();

    /**
     * Sets the message context.
     *
     * @param messageContext the message context
     */
    @Context
    public void setMessageContext(MessageContext messageContext) {

        servletRequest.set(messageContext.getHttpServletRequest());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response handleRequest(Message message, ClassResourceInfo classResourceInfo) {

        buildContext(servletRequest.get())
                .setRequestMessage(message)
                .build();

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response handleResponse(Message message, OperationResourceInfo operationResourceInfo, Response response) {

        buildContext(servletRequest.get())
                .setResponseMessage(message)
                .setResponse(response)
                .build();

        return null;
    }
}
