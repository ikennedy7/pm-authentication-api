package com.heb.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
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
@ComponentScan
public class AuthenticationApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}
	/**
	 *
	 * @param application
	 * @return
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AuthenticationApplication.class);
	}

	/**
	 * Message source message source.
	 *
	 * @return the message source
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
	 * Primary data source data source.
	 *
	 * @return the data source
	 */
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.primary")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	/**
	 * Secondary data source data source.
	 *
	 * @return the data source
	 */
	@Bean
	@Profile("local,dev,cert,prod")
	@ConfigurationProperties(prefix = "datasource.arbaf")
	public DataSource secondaryDataSource() {
		return DataSourceBuilder.create().build();
	}


}
