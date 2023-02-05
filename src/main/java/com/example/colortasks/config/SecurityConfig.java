package com.example.colortasks.config;

import com.example.colortasks.service.UserDetailsServiceImpl;
import com.example.colortasks.session.SessionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final SessionFilter sessionFilter;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, SessionFilter sessionFilter) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.sessionFilter = sessionFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.exceptionHandling().authenticationEntryPoint(
                (httpServletRequest, httpServletResponse, e) ->
                        httpServletResponse.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                e.getMessage())
        ).and();
        http.authorizeRequests()
                .antMatchers("/sch/registration", "/sch/login").permitAll()
                .anyRequest().authenticated();
        http.addFilterBefore(
                sessionFilter, UsernamePasswordAuthenticationFilter.class
        )
                .logout()
                .logoutUrl("/sch/logout")
                .logoutSuccessUrl("/sch/user");
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
