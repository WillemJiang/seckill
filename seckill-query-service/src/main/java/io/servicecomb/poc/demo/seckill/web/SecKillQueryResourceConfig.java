package io.servicecomb.poc.demo.seckill.web;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import javax.annotation.PostConstruct;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

//for spring boot test
@Configuration
@Primary
public class SecKillQueryResourceConfig extends ResourceConfig {
  @Value("${spring.jersey.application-path:/}")
  private String apiPath;

  public SecKillQueryResourceConfig() {
    registerClasses(SeckillQueryRESTEndpoint.class);
    property(ServletProperties.FILTER_FORWARD_ON_404, true);
  }

  @PostConstruct
  public void init() {
    // The init method is called
    configureSwagger();
  }

  private void configureSwagger() {
    register(ApiListingResource.class);
    register(SwaggerSerializers.class);

    // Just setup the configuration of the swagger API
    BeanConfig config = new BeanConfig();
    config.setConfigId("SecKill Query Service");
    config.setTitle("SecKill Query Service");
    config.setVersion("v1");
    config.setSchemes(new String[] {"http"});
    config.setBasePath(apiPath);
    config.setResourcePackage("io.servicecomb");
    config.setPrettyPrint(true);
    config.setScan(true);
  }
}
