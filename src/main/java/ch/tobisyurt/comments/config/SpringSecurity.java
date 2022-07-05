package ch.tobisyurt.comments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String[] resources = new String[]{
                "/api/**",
                "/","/static/**", "/apple-touch-icon.png", "/index.html", "/static/css/**",
                "/static/js/**", "/favicon-32x32.png", "/favicon-16x16.png", "/site.webmanifest",
                "/browserconfig.xml", "/favicon.ico",
                "/mstile-70x70.png", "mstile-144x144.png", "/mstile-150x150.png", "/mstile-310x150.png",
                "/mstile-310x310.png", "/safari-pinned-tab.svg", "/android-chrome-512x512.png",
                "/android-chrome-192x192.png"
        };


        // Most things configured here do not apply to the main site: tobisyurt.net, because it is served
        // by another webserver...
        http    .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(resources).permitAll().anyRequest().authenticated().and().httpBasic()
                .and()
                .headers()
                .xssProtection();
                /*.and()
                .contentSecurityPolicy("script-src 'self'");
*/
        return http.build();
    }

}
