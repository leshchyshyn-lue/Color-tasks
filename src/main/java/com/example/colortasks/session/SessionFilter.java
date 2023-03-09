package com.example.colortasks.session;

import com.example.colortasks.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SessionFilter extends OncePerRequestFilter {
    private final SessionRegistry sessionRegistry;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public SessionFilter(SessionRegistry sessionRegistry, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.sessionRegistry = sessionRegistry;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final String sessionId = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(sessionId == null || sessionId.length() == 0){
            chain.doFilter(request, response);
            return;
        }

        final String username = sessionRegistry.getUsernameForSession(sessionId);
        if (username == null){
            chain.doFilter(request, response);
            return;
        }

        final UserDetails currentUser = userDetailsServiceImpl.loadUserByUsername(username);
        final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                currentUser.getAuthorities()
        );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }




}
