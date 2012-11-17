package org.jboss.arquillian.quickstart.resteasy.service.rs;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.quickstart.resteasy.application.StockApplication;
import org.jboss.arquillian.quickstart.resteasy.model.Stock;
import org.jboss.arquillian.quickstart.resteasy.service.StockService;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.ClientAction;
import org.jboss.arquillian.warp.ServerAssertion;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.client.filter.HttpRequest;
import org.jboss.arquillian.warp.client.filter.RequestFilter;
import org.jboss.arquillian.warp.extension.rest.api.HttpMethod;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.extension.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 *
 */
@WarpTest
@RunWith(Arquillian.class)
public class StockServiceAjaxTestCase {

    /**
     * Creates the test deployment.
     *
     * @return the test deployment
     */
    @Deployment
    @OverProtocol("Servlet 3.0")
    public static Archive createTestArchive() {
        File[] libs = DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.easytesting:fest-assert:1.4").resolveAsFiles();

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(StockApplication.class, Stock.class, StockService.class, StockServiceResource.class)
                .addAsWebInfResource("WEB-INF/web.xml")
                .addAsWebResource("restclient.jsp")
                .addAsWebResource("js/jquery-1.8.2.min.js", "js/jquery-1.8.2.min.js")
                .addAsLibraries(libs);
    }

    /**
     * The context path of the deployed application.
     */
    @ArquillianResource
    private URL contextPath;

    /**
     * The web driver instance.
     */
    @Drone
    WebDriver browser;

    @Test
    @RunAsClient
    public void testAjaxGetStocks() {

        Warp.filter(new GetStocksFilter()).execute(new ClientAction() {
            @Override
            public void action() {

                browser.navigate().to(contextPath + "restclient.jsp");
            }
        }).verify(new ServerAssertion() {

            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext;

            @AfterServlet
            public void testGetStocks() {

                assertThat(restContext.getRequest().getMethod()).isEqualTo(HttpMethod.GET);
                assertThat(restContext.getResponse().getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
                assertThat(restContext.getResponse().getContentType()).isEqualTo("application/json");

                List list = (List) restContext.getResponse().getEntity();
                assertThat(list.size()).isEqualTo(1);
            }
        });
    }

    @Test
    @RunAsClient
    public void testAjaxGetStock() {

        browser.navigate().to(contextPath + "restclient.jsp");

        Warp.filter(new GetStockFilter()).execute(new ClientAction() {
            @Override
            public void action() {

                browser.findElement(By.className("stockLink")).click();
            }
        }).verify(new ServerAssertion() {

            private static final long serialVersionUID = 1L;

            @ArquillianResource
            private RestContext restContext;

            @AfterServlet
            public void testGetStock() {

                assertThat(restContext.getRequest().getMethod()).isEqualTo(HttpMethod.GET);
                assertThat(restContext.getResponse().getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
                assertThat(restContext.getResponse().getContentType()).isEqualTo("application/json");

                Stock stock = (Stock) restContext.getResponse().getEntity();
                assertThat(stock.getId()).isEqualTo(1L);
                assertThat(stock.getName()).isEqualTo("Acme");
                assertThat(stock.getCode()).isEqualTo("ACM");
            }
        });
    }

    private class GetStocksFilter implements RequestFilter<HttpRequest> {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(HttpRequest httpRequest) {
            return httpRequest.getUri().endsWith("/stocks");
        }
    }

    private class GetStockFilter implements RequestFilter<HttpRequest> {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean matches(HttpRequest httpRequest) {
            return httpRequest.getUri().endsWith("/stocks/1");
        }
    }
}

