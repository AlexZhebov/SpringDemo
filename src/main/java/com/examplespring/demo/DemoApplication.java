package com.examplespring.demo;

import com.examplespring.demo.controllers.ajaxpersonsController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;

@SpringBootApplication
public class DemoApplication extends WebMvcConfigurerAdapter {

	@Bean
	@Primary
	@ConfigurationProperties(prefix="jdbcexample.datasource")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/css/**")) {
			registry.addResourceHandler("/css/**").addResourceLocations("classpath:/css/");
		}
		if (!registry.hasMappingForPattern("/js/**")) {
			registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
		}
		if (!registry.hasMappingForPattern("/img/**")) {
			registry.addResourceHandler("/img/**").addResourceLocations("classpath:/img/");
		}
	}
}
