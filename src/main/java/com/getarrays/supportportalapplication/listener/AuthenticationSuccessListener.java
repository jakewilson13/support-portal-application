package com.getarrays.supportportalapplication.listener;

import com.getarrays.supportportalapplication.model.UserPrinciple;
import com.getarrays.supportportalapplication.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {

    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrinciple) {
            UserPrinciple user = (UserPrinciple) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptToCache(user.getUsername());
        }
    }
}
