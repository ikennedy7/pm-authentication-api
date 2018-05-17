package com.heb.authentication.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.authentication.security.jwt.JWTAuthenticationFilter;
import com.heb.authentication.security.jwt.JWTLoginFilter;
import com.heb.authentication.security.jwt.SkipPathRequestMatcher;
import com.heb.authentication.security.jwttoken.JwtAuthenticationProvider;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides security configuration.
 *
 * @author p235969
 * @since 11/7/14.
 */
@Profile({"dev", "cert"})
@Configuration
@ConfigurationProperties
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class LDAPSecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
	private AuthenticationManager authenticationManager;


    @Value("${spring.application.name}")
    private String realmName;
    @Value(value = "${heb.ldap.url}")
    private String url;
    @Value(value = "${heb.ldap.managerDn}")
    private String managerDn;
    @Value(value = "${heb.ldap.managerPassword}")
    private String managerPassword;
    @Value(value = "${heb.ldap.userSearchBase}")
    private String userSearchBase;
    @Value(value = "${heb.ldap.userSearchFilter}")
    private String userSearchFilter;

    public static final String TOKEN_REFRESH_ENTRY_POINT = "/token";
    public static final String FORM_BASED_LOGIN_ENTRY_POINT = "/login";
    public static final String FORM_BASED_LOGOUT_POINT = "/logout";
    public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/**";

    @Autowired
    private EntryPointHandler entryPointHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AuthenticationLogoutSuccessHandler authenticationLogoutSuccessHandler;

    @Autowired
	private ObjectMapper objectMapper;

    @Autowired
	private JwtAuthenticationProvider jwtAuthenticationProvider;




    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/token");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.csrf().disable().authorizeRequests()
             .antMatchers("/token").permitAll().antMatchers(HttpMethod.POST, "/login").permitAll()
               // .antMatchers(HttpMethod.GET, "/token").permitAll()
                .anyRequest().authenticated()
                .and()
                // We filter the api/login requests
                .addFilterBefore(loginFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                // And filter other requests to check the presence of JWT in header
                .addFilterBefore(AuthFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutSuccessHandler(this.authenticationLogoutSuccessHandler)
                //.deleteCookies("JSESSIONID")
                .deleteCookies("Authorization")
                .deleteCookies("refresh_token")
                .invalidateHttpSession(true)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);



/*        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/")
                .permitAll()
                .antMatchers(HttpMethod.GET, "*//**")
                .permitAll()
                .antMatchers(HttpMethod.POST, "*//**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                // JWT Filters
                .addFilterBefore(
                        new JWTLoginFilter(AppConstants.URL_LOGIN, authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        new JWTAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                // Logout
                .logout()
                .logoutSuccessHandler(this.authenticationLogoutSuccessHandler)
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                // Sessions
                .sessionManagement()
                .maximumSessions(1);*/
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .contextSource()
                .url(this.url)
                .managerDn(this.managerDn)
                .managerPassword(this.managerPassword)
                .and()
                .userSearchBase(this.userSearchBase)
                .userSearchFilter(this.userSearchFilter)
                .userDetailsContextMapper(new HebUserDetailsMapper())
                .ldapAuthoritiesPopulator(this.customAuthoritiesPopulator());
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    /**
     * Arbaf data source data source.
     *
     * @return the data source
     */
    @Bean(name = "arbafDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.arbaf")
    public DataSource arbafDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Arbaf dao jdbc template.
     *
     * @return the jdbc template
     */
    @Bean(name = "arbafDao")
    public JdbcTemplate arbafDao() {
        return new JdbcTemplate(this.arbafDataSource());
    }

    /**
     * Custom authorities populator ldap authorities populator.
     *
     * @return the ldap authorities populator
     */
    @Bean
    public LdapAuthoritiesPopulator customAuthoritiesPopulator() {
        CustomAuthoritiesPopulator customAuthoritiesPopulator = new CustomAuthoritiesPopulator();
        customAuthoritiesPopulator.setArbafDao(this.arbafDao());
        return customAuthoritiesPopulator;
    }




    /**
     * Heb ldap user service heb ldap user service.
     *
     * @return the heb ldap user service
     */
    @Bean
    public HebLdapUserService hebLdapUserService() {
        HebLdapUserService userDetailsService = new HebLdapUserService();
        List<LdapUserSearch> searchFinders = new ArrayList<>();
        searchFinders.add(this.hebSearch());
        userDetailsService.setUserFinders(searchFinders);
        userDetailsService.setUserMapper(this.userDetailsMapper());
        userDetailsService.setAuthPopulator(this.authoritiesPopulator());
        return userDetailsService;
    }

    /**
     * Heb search heb ldap user search.
     *
     * @return the heb ldap user search
     */
    @Bean
    public HebLdapUserSearch hebSearch() {
        HebLdapUserSearch hebLdapUserSearch = new HebLdapUserSearch(this.getContextSource());
        hebLdapUserSearch.setSearchBasePattern(this.userSearchBase);
        hebLdapUserSearch.setSearchFilter(this.userSearchFilter);
        return hebLdapUserSearch;
    }

    /**
     * Get context source default spring security context source.
     *
     * @return the default spring security context source
     */
    @Bean
    public DefaultSpringSecurityContextSource getContextSource() {
        DefaultSpringSecurityContextSource dctx = new DefaultSpringSecurityContextSource(this.url);
        dctx.setUserDn(this.managerDn);
        dctx.setPassword(this.managerPassword);
        return dctx;
    }

    /**
     * User details mapper heb user details mapper.
     *
     * @return the heb user details mapper
     */
    @Bean
    public HebUserDetailsMapper userDetailsMapper() {
        return new HebUserDetailsMapper();
    }

    /**
     * Authorities populator heb authorities populator.
     *
     * @return the heb authorities populator
     */
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
}


