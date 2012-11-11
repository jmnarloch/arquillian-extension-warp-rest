package org.jboss.arquillian.warp.extension.rest.resteasy.integration;

import org.jboss.arquillian.warp.extension.rest.api.HttpRequest;
import org.jboss.arquillian.warp.extension.rest.api.HttpResponse;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.HttpRequestImpl;
import org.jboss.arquillian.warp.extension.rest.spi.HttpResponseImpl;
import org.jboss.arquillian.warp.extension.rest.spi.RestContextImpl;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;

/**
 *
 */
public class ResteasyContextBuilder {

    private ResourceMethod resourceMethod;

    private ServerResponse serverResponse;

    private Object requestEntity;

    public ResteasyContextBuilder() {

        // empty constructor
    }

    public ResteasyContextBuilder setResourceMethod(ResourceMethod resourceMethod) {

        this.resourceMethod = resourceMethod;
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

        return new HttpRequestImpl();
    }

    private HttpResponse buildHttpResponse() {

        return new HttpResponseImpl();
    }
}
