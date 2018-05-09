package com.heb.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing
@EntityScan
@ComponentScan({"com.heb.authentication.webservice",
		"com.heb.authentication.service",
		"com.heb.authentication.mapper",
		"com.heb.authentication.util",
		"com.heb.authentication.repository",
		"com.heb.authentication.restservice",
		"com.heb.authentication.jms",
		"com.heb.authentication.task"})
public class AuthenticationApplicationTests extends SpringBootServletInitializer {

	/**
	 * Spring Boot Runner.
	 *
	 * @param args Optional parameters from command-line.
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplicationTests.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AuthenticationApplicationTests.class);
	}

	/**
	 * Messages.properties
	 */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(3600);
		messageSource.setConcurrentRefresh(true);

		return messageSource;
	}

	/**
	 * H2 datasource
	 */
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.primary")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}
}
