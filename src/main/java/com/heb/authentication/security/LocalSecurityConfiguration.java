package com.heb.authentication.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.authentication.security.jwt.JWTAuthenticationFilter;
import com.heb.authentication.security.jwt.JWTLoginFilter;
import com.heb.authentication.security.jwt.SkipPathRequestMatcher;
import com.heb.authentication.security.jwttoken.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is an extension of {@link WebSecurityConfigurerAdapter} providing local-only security configuration.
 *
 * @author k7923891
 * @since 5/20/18
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
	private HebUserService hebUserService;

//	@Autowired
//	private CustomAuthenticationProvider customAuthProvider;

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
		auth.userDetailsService(hebUserService);
		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> manager = auth.inMemoryAuthentication();
		List<GrantedAuthority> authorities = new ArrayList<>();
		manager.withUser(new HebUserData("user", "{noop}user", true, true, true, true, authorities));
		auth.authenticationProvider(jwtAuthenticationProvider);
	}

	@Bean
	public JWTLoginFilter loginFilter() throws Exception {
		JWTLoginFilter jwtLoginFilter = new JWTLoginFilter("/login",authenticationSuccessHandler,authenticationFailureHandler,objectMapper );
		jwtLoginFilter.setAuthenticationManager(authenticationManagerBean());
		return jwtLoginFilter;
	}

	@Bean
	public JWTAuthenticationFilter AuthFilter()throws Exception{
		List<String> pathsToSkip = Arrays.asList(TOKEN_REFRESH_ENTRY_POINT, FORM_BASED_LOGIN_ENTRY_POINT,FORM_BASED_LOGOUT_POINT );
		SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, TOKEN_BASED_AUTH_ENTRY_POINT);
		JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationFailureHandler, matcher);
		jwtAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
		return jwtAuthenticationFilter;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
