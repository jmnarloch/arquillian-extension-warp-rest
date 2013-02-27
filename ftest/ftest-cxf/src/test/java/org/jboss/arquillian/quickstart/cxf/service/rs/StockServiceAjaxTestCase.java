package org.jboss.arquillian.quickstart.cxf.service.rs;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.quickstart.cxf.application.StockApplication;
import org.jboss.arquillian.quickstart.cxf.model.Stock;
import org.jboss.arquillian.quickstart.cxf.service.StockService;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.extension.rest.api.HttpMethod;
import org.jboss.arquillian.warp.extension.rest.api.RestContext;
import org.jboss.arquillian.warp.servlet.AfterServlet;
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
import static org.jboss.arquillian.warp.client.filter.http.HttpFilters.request;

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
                .loadMetadataFromPom("pom.xml")
                .artifacts("org.apache.cxf:cxf-rt-frontend-jaxrs")
                .artifacts("org.apache.cxf:cxf-rt-rs-extension-providers")
                .artifacts("org.codehaus.jettison:jettison")
                .artifacts("org.springframework:spring-web")
                .resolveAsFiles();

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(StockApplication.class, Stock.class, StockService.class, StockServiceResource.class)
                .addAsWebInfResource("WEB-INF/web.xml")
                .addAsWebInfResource("WEB-INF/cxf.xml")
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

        Warp.initiate(new Activity() {

            @Override
            public void perform() {

                browser.navigate().to(contextPath + "restclient.jsp");
            }
        }).group().observe(request().uri().endsWith("/stocks")).inspect(new Inspection() {

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

        Warp.initiate(new Activity() {

            @Override
            public void perform() {

                browser.findElement(By.className("stockLink")).click();
            }
        }).group().observe(request().uri().endsWith("/stocks/1")).inspect(new Inspection() {

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
}