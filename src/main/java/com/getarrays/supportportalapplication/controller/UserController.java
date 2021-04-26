package com.getarrays.supportportalapplication.controller;


import com.getarrays.supportportalapplication.exception.model.EmailExistsException;
import com.getarrays.supportportalapplication.exception.model.ExceptionHandling;
import com.getarrays.supportportalapplication.exception.model.UserNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/", "/user"})
//exception handling will contain all of our exceptions
public class UserController extends ExceptionHandling {

    @GetMapping(value = "/home")
    public String hello() throws UserNotFoundException {

        //return "hello";
        throw new UserNotFoundException("User was not found");
    }
}
