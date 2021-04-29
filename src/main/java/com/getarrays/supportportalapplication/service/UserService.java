package com.getarrays.supportportalapplication.service;


import com.getarrays.supportportalapplication.exception.model.EmailExistsException;
import com.getarrays.supportportalapplication.exception.model.EmailNotFoundException;
import com.getarrays.supportportalapplication.exception.model.UsernameExistsException;
import com.getarrays.supportportalapplication.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    //register is when you do not have an account so you have to signup
    User register(String firstName, String lastName, String username, String email) throws EmailExistsException, UsernameExistsException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    //when you're already logged into the application and try to add a new user
    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistsException, UsernameExistsException, IOException;

    //updating information
    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistsException, UsernameExistsException, IOException;

    void deleteUser(long id);

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    //only updating the profile picture and that's it
    User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistsException, UsernameExistsException, IOException;


}
