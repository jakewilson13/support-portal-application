package com.getarrays.supportportalapplication.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.getarrays.supportportalapplication.constant.SecurityConstant;
import com.getarrays.supportportalapplication.model.UserPrinciple;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

//whenever apps starts we'll have a bean for jwt token provider
@Component
public class JwtTokenProvider {

    //normally will have this inside of a secure server & an property file and get the information when you need it
    @Value("${jwt.secret}")    //going to come from application.properties which is why we use @Value
    private String secret;

    //we want to be able to take in the data that spring needs
    public String generateJwtToken(UserPrinciple userPrinciple) {
        String[] claims = getClaimsFromUser(userPrinciple);
        return JWT.create().withIssuer(SecurityConstant.GET_ARRAYS_LLC)
                .withAudience(SecurityConstant.GET_ARRAYS_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrinciple.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        //stream loops through a collection
        //map is used to transform every single item inside of the collection
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    //telling spring security to get the authentication of the user after verifying the token
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        //sets up info about the user in spring security context
        userPasswordToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordToken;
    }

    //check to see if token is valid
    public boolean isTokenValid(String username, String token) {
        JWTVerifier jwtVerifier = getJwtVerifier();
        //returns true or false if its empty or not
        return StringUtils.isNotEmpty(username) && !isTokenExpired(jwtVerifier, token);
    }

    public String getSubject(String token) {
        JWTVerifier jwtVerifier = getJwtVerifier();
        return jwtVerifier.verify(token).getSubject();
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJwtVerifier();
        return verifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJwtVerifier() {
        JWTVerifier jwtVerifier;

        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            jwtVerifier = JWT.require(algorithm).withIssuer(SecurityConstant.GET_ARRAYS_LLC).build();
            //wont pass information from the acutal exception cause that can reveal internal workings of application
        } catch (JWTVerificationException exception) {
            //creating a new instance of exception and passing in our implementation
            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
        }
        return jwtVerifier;
    }

    private String[] getClaimsFromUser(UserPrinciple userPrinciple) {
        List<String> authorities = new ArrayList<>();
        //granted authority is coming from the userPrinciple
        for(GrantedAuthority grantedAuthority :userPrinciple.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        //returns it as a array of string
        return authorities.toArray(new String[0]);
    }

    private boolean isTokenExpired(JWTVerifier jwtVerifier, String token) {
        Date expiration = jwtVerifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }
}
