package com.getarrays.supportportalapplication.listener;

import com.getarrays.supportportalapplication.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

//this will fire when a user fails to log into our application
@Component
public class AuthenticationFailureListener {

    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener  //listening on the event. Whenever it occurs we grab the principal(the username) and then we add the user into the actual cache if they fail to login
    public void authenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();    //safe check because we don't want the application to trip if it's not an actual String
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}