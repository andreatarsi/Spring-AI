package spring.ai.example.spring_ai_demo.web.ai.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*// 1. ESPONE IL DATABASE H2 SUL BROWSER
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<JakartaWebServlet> bean =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
        bean.addInitParameter("webAllowOthers", "true");
        bean.setLoadOnStartup(1);
        return bean;
    }*/

    // 2. DISABILITA LA SICUREZZA PER I TEST
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll() // Permette a Postman e al Frontend di chiamare le API senza password
                )
                // Disabilitiamo il CSRF su TUTTE le API, non solo su alcune, per permettere le chiamate POST/PUT/DELETE
                .csrf(csrf -> csrf.disable())

                // Necessario per far caricare l'interfaccia grafica di H2 (che usa gli iFrame)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}