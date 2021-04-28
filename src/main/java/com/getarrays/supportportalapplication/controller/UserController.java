package com.getarrays.supportportalapplication.controller;


import com.getarrays.supportportalapplication.exception.model.EmailExistsException;
import com.getarrays.supportportalapplication.exception.model.ExceptionHandling;
import com.getarrays.supportportalapplication.exception.model.UserNotFoundException;
import com.getarrays.supportportalapplication.exception.model.UsernameExistsException;
import com.getarrays.supportportalapplication.model.User;
import com.getarrays.supportportalapplication.model.UserPrinciple;
import com.getarrays.supportportalapplication.provider.JwtTokenProvider;
import com.getarrays.supportportalapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static com.getarrays.supportportalapplication.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(value = {"/", "/user"})
//exception handling will contain all of our exceptions
public class UserController extends ExceptionHandling {

    private UserService userService;
    private AuthenticationManager authnManager;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authnManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authnManager = authnManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<User> login(@RequestBody User user) {  //we take the user information
        authenticate(user.getUsername(), user.getPassword());   //pass the information to the authenticate method
        User loginUser = userService.findUserByUsername(user.getUsername());    //find the user
        UserPrinciple userPrinciple = new UserPrinciple(loginUser);     //create a userPrinciple
        HttpHeaders jwtHeader = getJwtHeader(userPrinciple);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistsException, UsernameExistsException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }



    private HttpHeaders getJwtHeader(UserPrinciple userPrinciple) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrinciple)); //takes in the name of the actual header & the value
        return headers;
    }

    //if anything is wrong with the information that they provided
    private void authenticate(String username, String password) {
        authnManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)); //authenticating a user through spring security(it takes in a user principle which we create in the post request)
    }
}
