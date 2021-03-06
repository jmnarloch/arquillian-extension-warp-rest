/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.warp.extension.jaxrs.spi;

/**
 * An utility class that defines constants used in this extension.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class WarpRestCommons {

    /**
     * Represents the name of the attribute which is used for storing the {@link
     * org.jboss.arquillian.warp.extension.jaxrs.api.RestContext} in the servlet request.
     */
    public static final String WARP_REST_ATTRIBUTE = "org.jboss.arquillian.warp.extension.jaxrs.spi.WARP_REST_ATTRIBUTE";
}
