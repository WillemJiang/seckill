package io.servicecomb.poc.demo;

import io.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class CommandServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CommandServiceApplication.class, args);
  }

  @EnableServiceComb
  @Profile("cse")
  @Configuration
  static class ServiceCombConfig {
  }
}
