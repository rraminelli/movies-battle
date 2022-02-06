package br.com.rraminelli.moviesbattle.userauth.application.config;

import br.com.rraminelli.moviesbattle.userauth.application.security.CustomUserDetailsService;
import br.com.rraminelli.moviesbattle.userauth.application.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Profile("!TEST_MOCK")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence password) {
                try {
                    MessageDigest crypt = MessageDigest.getInstance("SHA-1");
                    crypt.reset();
                    crypt.update(password.toString().getBytes("UTF-8"));
                    return new BigInteger(1, crypt.digest()).toString(16);
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                    return "";
                }
            }
            @Override
            public boolean matches(CharSequence passwordStr, String passwordEncoder) {
                return passwordEncoder.equals(encode(passwordStr));
            }
        };
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));

        http
                .cors().configurationSource(request -> corsConfiguration)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers("/auth-server/**", "/auth/**", "/h2-console/**")
                        .permitAll()
                    .antMatchers(getWhiteList().toArray(String[]::new))
                        .permitAll()
                    .anyRequest()
                        .authenticated();
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.csrf().disable();
    }

    private List<String> getWhiteList() {
        return "prod".equals(activeProfile) ?
                new ArrayList<>() :
                List.of("/v2/api-docs", "/v3/api-docs", "/swagger-resources/**",
                        "/swagger-ui/**", "/h2-console/**", "/favicon.ico");
    }
}
