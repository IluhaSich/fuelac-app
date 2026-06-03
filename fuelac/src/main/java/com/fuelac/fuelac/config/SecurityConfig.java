package com.fuelac.fuelac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Аутентификация — доступна всем
                        .requestMatchers("/api/auth/**").permitAll()
                        // Администрирование организаций и пользователей — только администратор
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Ведение справочников (ТС, водители, нормы расхода): создание,
                        // изменение и удаление — только менеджер организации
                        // (справочники привязаны к организации; администратор ведёт
                        // лишь организации и пользователей)
                        .requestMatchers(HttpMethod.POST, "/api/vehicles", "/api/drivers", "/api/fuel-norms")
                                .hasRole("ORGANIZATION_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/vehicles/**", "/api/drivers/**", "/api/fuel-norms/**")
                                .hasRole("ORGANIZATION_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/vehicles/**", "/api/drivers/**", "/api/fuel-norms/**")
                                .hasRole("ORGANIZATION_MANAGER")
                        // Ведение путевых листов: создание, изменение, удаление и закрытие —
                        // диспетчер или менеджер организации
                        .requestMatchers(HttpMethod.POST, "/api/waybills")
                                .hasAnyRole("DISPATCHER", "ORGANIZATION_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/waybills/*/close")
                                .hasAnyRole("DISPATCHER", "ORGANIZATION_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/waybills/**")
                                .hasAnyRole("DISPATCHER", "ORGANIZATION_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/waybills/**")
                                .hasAnyRole("DISPATCHER", "ORGANIZATION_MANAGER")
                        // Прочие операции (чтение, поиск, выгрузка отчётов) —
                        // любой аутентифицированный пользователь
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
