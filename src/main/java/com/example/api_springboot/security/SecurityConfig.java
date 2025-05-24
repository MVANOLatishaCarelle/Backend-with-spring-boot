package com.example.api_springboot.security;


import com.example.api_springboot.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService CustomUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics
                .requestMatchers(HttpMethod.POST, "/client", "/vendeur").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/plat/{nom}", "/api/plat/disponibles").permitAll()
                .requestMatchers(HttpMethod.POST, "/client/auth", "/vendeur/auth").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/client/auth", "/api/client").permitAll()
                // Endpoints nécessitant une authentification
                .requestMatchers(HttpMethod.GET, "/client", "/vendeur").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/client","/vendeur").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/client", "/vendeur").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/plat").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/plat/mes-plats", "/api/plat/mes-plats/recherche").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/commande").authenticated()

                .anyRequest().permitAll()
            )
            // Ajout du filtre JWT avant le filtre d'authentification standard
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://192.168.137.1")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(CustomUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    // public void configure(AuthenticationManagerBuilder auth) throws Exception {
    //     auth.userDetailsService(vendeurDetailsService)
    //         .passwordEncoder(passwordEncoder());
    // }
}
