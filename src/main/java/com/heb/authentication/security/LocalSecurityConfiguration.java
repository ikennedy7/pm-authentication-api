package com.heb.authentication.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.authentication.security.jwt.JWTAuthenticationFilter;
import com.heb.authentication.security.jwt.JWTLoginFilter;
import com.heb.authentication.security.jwt.SkipPathRequestMatcher;
import com.heb.authentication.security.jwttoken.JwtAuthenticationProvider;
import com.heb.authentication.security.model.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static com.heb.authentication.security.LDAPSecurityConfiguration.FORM_BASED_LOGIN_ENTRY_POINT;
import static com.heb.authentication.security.LDAPSecurityConfiguration.TOKEN_REFRESH_ENTRY_POINT;

/**
 * This is an extension of {@link WebSecurityConfigurerAdapter} providing local-only security configuration.
 *
 * @author p235969
 * @since 11/7/14.
 */
@Profile({"local"})
@Configuration
@ConfigurationProperties
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class LocalSecurityConfiguration extends WebSecurityConfigurerAdapter {

	public static final String TOKEN_REFRESH_ENTRY_POINT = "/token";
	public static final String FORM_BASED_LOGIN_ENTRY_POINT = "/login";
	public static final String FORM_BASED_LOGOUT_POINT = "/logout";
	public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/**";


	@Value("${spring.application.name}")
	private String realmName;

	@Autowired
	private CustomAuthenticationProvider customAuthProvider;

	@Autowired
	private JwtAuthenticationProvider jwtAuthenticationProvider;


	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	private AuthenticationLogoutSuccessHandler authenticationLogoutSuccessHandler;

	@Autowired
	private ObjectMapper objectMapper;

	protected void configure(HttpSecurity http) throws Exception{
		http.csrf().disable().authorizeRequests()
				.antMatchers("/token").permitAll()
				.antMatchers(HttpMethod.POST, "/login")
				.permitAll().anyRequest().authenticated();

	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> manager = auth.inMemoryAuthentication();
		manager.withUser("user").password("{noop}user").roles("USER");
		manager.withUser("vendor").password("{noop}vendor").roles("USER", "VENDOR");
		manager.withUser("admin").password("{noop}admin").roles("ADMIN").disabled(true);
		auth.authenticationProvider(jwtAuthenticationProvider);
	}

	@Bean
	public HebAuthoritiesPopulator authoritiesPopulator() {
		HebAuthoritiesPopulator hebAuthoritiesPopulator = new HebAuthoritiesPopulator();
		hebAuthoritiesPopulator.setApplAbb(this.realmName);
		hebAuthoritiesPopulator.setArbafDao(this.arbafDao());
		return hebAuthoritiesPopulator;
	}

	@Bean
	public JWTLoginFilter loginFilter(){
		JWTLoginFilter jwtLoginFilter = new JWTLoginFilter("/login",authenticationSuccessHandler,authenticationFailureHandler,objectMapper );
		jwtLoginFilter.setAuthenticationManager(this.authenticationManager);
		return jwtLoginFilter;
	}

	@Bean
	public JWTAuthenticationFilter AuthFilter()throws Exception{
		List<String> pathsToSkip = Arrays.asList(TOKEN_REFRESH_ENTRY_POINT, FORM_BASED_LOGIN_ENTRY_POINT,FORM_BASED_LOGOUT_POINT );
		SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, TOKEN_BASED_AUTH_ENTRY_POINT);
		JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationFailureHandler, matcher);
		jwtAuthenticationFilter.setAuthenticationManager(this.authenticationManager);
		return jwtAuthenticationFilter;
	}

	@Bean(name = "arbafDao")
	public JdbcTemplate arbafDao() {
		return new JdbcTemplate(this.arbafDataSource());
	}
	@Bean(name = "arbafDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.arbaf")
	public DataSource arbafDataSource() {
		return DataSourceBuilder.create().build();
	}
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
