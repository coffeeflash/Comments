package ch.tobisyurt.comments.config;

import ch.tobisyurt.comments.service.CommentsUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    CommentsUserDetailsService commentsUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {

        String[] resources = new String[]{
                "/","/static/**", "/favicon.ico", "/index.html", "/static/css/**","/static/fonts/**","/static/js/**",
                // TODO /api/** rausnehmen, temporär alles offen...
                "/api/**"
        };

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable().authorizeRequests()
                .antMatchers(resources).permitAll()
                .anyRequest().authenticated().and().httpBasic();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception
    {

        auth.userDetailsService(commentsUserDetailsService);
    }

}
