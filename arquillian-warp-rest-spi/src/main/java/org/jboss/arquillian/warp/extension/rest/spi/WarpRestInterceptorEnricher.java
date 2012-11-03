package org.jboss.arquillian.warp.extension.rest.spi;

import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 *
 */
public interface WarpRestInterceptorEnricher {

    /**
     * Enriches the web archive by providing JAX-RS interceptor that will intercept execution context.
     *
     * @param archive the web archive to enrich
     */
    void enrichWebArchive(WebArchive archive);
}
