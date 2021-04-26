package com.getarrays.supportportalapplication.filter;


import com.getarrays.supportportalapplication.constant.SecurityConstant;
import com.getarrays.supportportalapplication.provider.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// filter will fire everytime we get a new request, and will only fire once
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //have to make sure if the request is options
    //see's what http methods are supported by the server and provides them to the client
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //checking if request is option
        if(request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHOD)) {
            //value gets the actual number
            response.setStatus(HttpStatus.OK.value());


            //if it is not option then we execute the else
        } else {
            //try to get auth headers
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            //check if it's null or if it doesn't start with the prefix
            if(authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)) {

                //then we let the request continue it's course if it matches with the if
                filterChain.doFilter(request, response);
                return;

                //otherwise we will try to get the header
            }
            //removing the "bearer" from the header so we will just be left with the actual token
            String token = authorizationHeader.substring(SecurityConstant.TOKEN_PREFIX.length());

            //then we will try to get the username
            String username = jwtTokenProvider.getSubject(token);

            //if the token is valid and username is correct and they don't have an authentication in the security context holder
            if(jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {

                //should get the authorities for the user
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

                //should give us the authentication that we need
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);

                //once we get the authentication then we set it in the security context holder
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {

                //if anything fails then we will clear the context
                SecurityContextHolder.clearContext();
            }
        }
        //then we let the request continue it's course
        filterChain.doFilter(request, response);
    }
}
