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

import org.apache.cxf.message.Message;
import org.jboss.arquillian.warp.extension.rest.api.HttpMethod;
import org.jboss.arquillian.warp.extension.rest.api.HttpRequest;
import org.jboss.arquillian.warp.extension.rest.api.HttpResponse;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.HttpRequestImpl;
import org.jboss.arquillian.warp.extension.rest.spi.HttpResponseImpl;
import org.jboss.arquillian.warp.extension.rest.spi.RestContextBuilder;
import org.jboss.arquillian.warp.extension.rest.spi.RestContextImpl;

import javax.ws.rs.core.Response;

/**
 *
 */
public class CxfContextBuilder implements RestContextBuilder {

    private Message requestMessage;

    private Message responseMessage;

    private Response response;

    public CxfContextBuilder setRequestMessage(Message requestMessage) {

        this.requestMessage = requestMessage;
        return this;
    }

    public CxfContextBuilder setResponseMessage(Message responseMessage) {

        this.responseMessage = responseMessage;
        return this;
    }

    public CxfContextBuilder setResponse(Response response) {

        this.response = response;
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
        request.setContentType((String) requestMessage.get(Message.CONTENT_TYPE));
        request.setEntity(getRequestEntity());
        request.setHttpMethod(getRequestMethod((String) requestMessage.get(Message.HTTP_REQUEST_METHOD)));
        return request;
    }

    private HttpResponse buildHttpResponse() {

        HttpResponseImpl response = new HttpResponseImpl();

        if (responseMessage != null) {
            response.setContentType(this.response.getMediaType().toString());
            response.setStatusCode(this.response.getStatus());
            response.setEntity(this.response.getEntity());
        }

        return response;
    }

    public Object getRequestEntity() {
        return requestMessage.getContentFormats().size() > 0 ?
                requestMessage.getContent(requestMessage.getContentFormats().iterator().next()) : null;
    }

    private HttpMethod getRequestMethod(String methodName) {
        return Enum.valueOf(HttpMethod.class, methodName.toUpperCase());
    }
}
