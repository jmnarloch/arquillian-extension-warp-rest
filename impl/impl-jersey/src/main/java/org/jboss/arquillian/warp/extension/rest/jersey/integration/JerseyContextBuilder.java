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
import com.sun.jersey.spi.container.ContainerResponse;
import org.jboss.arquillian.warp.extension.rest.api.HttpMethod;
import org.jboss.arquillian.warp.extension.rest.api.HttpRequest;
import org.jboss.arquillian.warp.extension.rest.api.HttpResponse;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.HttpRequestImpl;
import org.jboss.arquillian.warp.extension.rest.spi.HttpResponseImpl;
import org.jboss.arquillian.warp.extension.rest.spi.RestContextBuilder;
import org.jboss.arquillian.warp.extension.rest.spi.RestContextImpl;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public class JerseyContextBuilder implements RestContextBuilder {

    private ContainerRequest containerRequest;

    private ContainerResponse containerResponse;

    public JerseyContextBuilder() {

        // empty constructor
    }

    public JerseyContextBuilder setContainerRequest(ContainerRequest containerRequest) {

        this.containerRequest = containerRequest;
        return this;
    }

    public JerseyContextBuilder setContainerResponse(ContainerResponse containerResponse) {

        this.containerResponse = containerResponse;
        return this;
    }

    public RestContext build() {

        RestContextImpl result = new RestContextImpl();
        result.setHttpRequest(buildHttpRequest());
        result.setHttpResponse(buildHttpResponse());
        return result;
    }

    private HttpRequest buildHttpRequest() {

        HttpRequestImpl request = new HttpRequestImpl();
        request.setContentType(getMediaTypeName(containerRequest.getMediaType()));
        // TODO this likely won't work
        request.setEntity(containerRequest.getEntity(Object.class));
        request.setHttpMethod(getHttpMethod(containerRequest.getMethod()));
        return request;
    }

    private HttpResponse buildHttpResponse() {

        HttpResponseImpl response = new HttpResponseImpl();

        if (containerResponse != null) {
            response.setContentType(getMediaTypeName(containerResponse.getMediaType()));
            response.setStatusCode(containerResponse.getStatus());
            response.setEntity(containerResponse.getEntity());
        }

        return response;
    }

    private String getMediaTypeName(MediaType mediaType) {
        return mediaType != null ? mediaType.toString() : null;
    }

    private static HttpMethod getHttpMethod(String methodName) {

        return Enum.valueOf(HttpMethod.class, methodName.toUpperCase());
    }
}
