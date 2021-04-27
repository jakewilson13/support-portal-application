package com.getarrays.supportportalapplication.service;


import com.getarrays.supportportalapplication.exception.model.EmailExistsException;
import com.getarrays.supportportalapplication.exception.model.UsernameExistsException;
import com.getarrays.supportportalapplication.model.User;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistsException, UsernameExistsException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);


}
