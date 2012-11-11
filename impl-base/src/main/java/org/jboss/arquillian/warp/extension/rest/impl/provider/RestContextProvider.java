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
package org.jboss.arquillian.warp.extension.rest.impl.provider;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.spi.WarpRestCommons;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

/**
 *
 */
public class RestContextProvider implements ResourceProvider {

    /**
     * Instance of {@link HttpServletRequest}.
     */
    private Instance<HttpServletRequest> httpServletRequestInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canProvide(Class<?> aClass) {

        return RestContext.class.equals(aClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object lookup(ArquillianResource arquillianResource, Annotation... annotations) {

        // TODO add error handling

        // retrieves the http request
        HttpServletRequest request = httpServletRequestInstance.get();

        // tries to retrieve the RestContext from the request and return it as a result
        return (RestContext)request.getAttribute(WarpRestCommons.WARP_REST_ATTRIBUTE);
    }
}
