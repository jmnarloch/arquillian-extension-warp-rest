package org.jboss.arquillian.warp.extension.rest.impl.client;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.rest.impl.container.WarpRestRemoteExtension;
import org.jboss.arquillian.warp.extension.rest.spi.WarpRestInterceptorEnricher;
import org.jboss.arquillian.warp.spi.WarpLifecycleExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.util.Collection;

/**
 *
 */
public class WarpRestExtension implements WarpLifecycleExtension {

    @Inject
    private Instance<ServiceLoader> serviceLoaderInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaArchive getEnrichmentLibrary() {

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);

        // adds the api classes
        archive.addPackage(RestContext.class.getPackage());

        // adds the spi classes
        archive.addPackage(WarpRestInterceptorEnricher.class.getPackage());

        // adds the implementation classes
        archive.addPackage(WarpRestRemoteExtension.class.getPackage());

        return archive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichWebArchive(WebArchive webArchive) {

        Collection<WarpRestInterceptorEnricher> enrichers =
                serviceLoaderInstance.get().all(WarpRestInterceptorEnricher.class);

        // for each registered enricher
        for (WarpRestInterceptorEnricher enricher : enrichers) {

            // enriches the deployment archive
            enricher.enrichWebArchive(webArchive);
        }
    }
}
