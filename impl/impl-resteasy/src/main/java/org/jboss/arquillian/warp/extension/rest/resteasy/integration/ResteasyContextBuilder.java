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
package org.jboss.arquillian.warp.extension.rest.resteasy.integration;

import org.jboss.arquillian.warp.extension.rest.api.HttpMethod;
import org.jboss.arquillian.warp.extension.rest.api.HttpRequest;
import org.jboss.arquillian.warp.extension.rest.api.HttpResponse;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.HttpRequestImpl;
import org.jboss.arquillian.warp.extension.rest.spi.HttpResponseImpl;
import org.jboss.arquillian.warp.extension.rest.spi.RestContextImpl;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public class ResteasyContextBuilder {

    private org.jboss.resteasy.spi.HttpRequest httpRequest;

    private ResourceMethod resourceMethod;

    private Object requestEntity;

    private MediaType responseMediaType;

    private ServerResponse serverResponse;

    public ResteasyContextBuilder() {

        // empty constructor
    }

    public ResteasyContextBuilder setHttpRequest(org.jboss.resteasy.spi.HttpRequest httpRequest) {

        this.httpRequest = httpRequest;
        return this;
    }

    public ResteasyContextBuilder setResourceMethod(ResourceMethod resourceMethod) {

        this.resourceMethod = resourceMethod;
        return this;
    }

    public ResteasyContextBuilder setResponseMediaType(MediaType responseMediaType) {

        this.responseMediaType = responseMediaType;
        return this;
    }

    public ResteasyContextBuilder setServerResponse(ServerResponse serverResponse) {

        this.serverResponse = serverResponse;
        return this;
    }

    public ResteasyContextBuilder setRequestEntity(Object entity) {

        this.requestEntity = entity;
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
        request.setContentType(getMediaTypeName(httpRequest.getHttpHeaders().getMediaType()));
        request.setEntity(this.requestEntity);
        request.setHttpMethod(getHttpMethod(httpRequest.getHttpMethod()));
        return request;
    }

    private HttpResponse buildHttpResponse() {

        HttpResponseImpl response = new HttpResponseImpl();
        response.setContentType(getMediaTypeName(responseMediaType));

        if (serverResponse != null) {
            response.setStatusCode(serverResponse.getStatus());
            response.setEntity(serverResponse.getEntity());
        }

        return response;
    }

    private static String getMediaTypeName(MediaType mediaType) {
        return mediaType != null ? mediaType.toString() : null;
    }

    private static HttpMethod getHttpMethod(String methodName) {

        return Enum.valueOf(HttpMethod.class, methodName.toUpperCase());
    }
}
