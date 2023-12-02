package com.mkr.server.config;

import com.mkr.server.auth.DataStoreUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/api/usertype",
                    "/api/auth",
                    "/api/postproductcomment",
                    "/api/deleteproducts"
                ).authenticated()
                .requestMatchers(
                    "/api/postproductcomment",
                    "/api/postusercomment",
                    "/api/userproducts",
                    "/api/userproductssearchdesc",
                    "/api/cartproducts",
                    "/api/addcartproduct",
                    "/api/cartproductamount",
                    "/api/cartproduct",
                    "/api/updateaccountinfo",
                    "/api/addproduct",
                    "/api/updateproduct",
                    "/api/checkout"
                ).hasRole("CUSTOMER_TRADER")
                .requestMatchers(
                    "/api/changeproductstatus",
                    "/api/getproductswaitingapproval"
                ).hasRole("ADMIN")
                .requestMatchers("/**", "/api/**").permitAll()
            )
            .httpBasic(basic ->
                basic.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
                })
            )
            .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new DataStoreUserDetailsService();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());

        return auth;
    }
}
