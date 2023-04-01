package br.com.jacto.trevo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SpringSecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.POST,  "/login", "/register").permitAll()
                .antMatchers(HttpMethod.PUT, "/accounts").permitAll()


                .antMatchers(HttpMethod.GET, "/products/orders/{id}").hasAnyRole("ADMINISTRADOR", "COLABORADOR")
                .antMatchers(HttpMethod.POST, "/products/**").hasAnyRole("ADMINISTRADOR", "COLABORADOR")
                .antMatchers(HttpMethod.PUT, "/products/**", "/images").hasAnyRole("ADMINISTRADOR", "COLABORADOR")
                .antMatchers(HttpMethod.DELETE, "/products/**", "/images").hasAnyRole("ADMINISTRADOR", "COLABORADOR")

                .antMatchers(HttpMethod.GET, "/orders/{id}").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                .antMatchers(HttpMethod.POST, "/orders").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                .antMatchers(HttpMethod.PUT, "/orders").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                .antMatchers(HttpMethod.DELETE, "/orders/**").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
