package com.getarrays.supportportalapplication.controller;


import com.getarrays.supportportalapplication.exception.model.*;
import com.getarrays.supportportalapplication.model.HttpResponse;
import com.getarrays.supportportalapplication.model.User;
import com.getarrays.supportportalapplication.model.UserPrinciple;
import com.getarrays.supportportalapplication.provider.JwtTokenProvider;
import com.getarrays.supportportalapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.getarrays.supportportalapplication.constant.FileConstant.*;
import static com.getarrays.supportportalapplication.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(value = {"/", "/user"})
//exception handling will contain all of our exceptions
public class UserController extends ExceptionHandling {

    public static final String EMAIL_SENT = "An Email with a new password was sent to: ";
    public static final String USER_SUCCESSFULLY_DELETED = "User successfully deleted";
    private UserService userService;
    private AuthenticationManager authnManager;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authnManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authnManager = authnManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping(value = "/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(HttpStatus.OK, EMAIL_SENT + email);
    }

    @GetMapping(value = "/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getUserProfileImage(@PathVariable("username")String username, @PathVariable("filename") String filename) throws IOException {    //getting the bytes from the image so it's returning an array of bytes
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));    //creating the path. Example: "user.home" + "/supportportal/user/rick(user_folder)/rick.jpg(the image)"
        //returns those bytes from the path to the browser, which the browser will be able to parse those bytes & return the folder
    }

    @GetMapping(value = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);  //getting the url, now we have to call it
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  //where we store all of the data that is going to come from the actual url
        try(InputStream inputStream = url.openStream()) {   //open the url, we open the stream which we have to convert to a bye[]
            int bytesRead;
            byte[] chunk = new byte[1024];  //a chuck of how many bytes we are reading at a time from the url
            while((bytesRead = inputStream.read(chunk)) > 0) {  //> 0, keep reading it until we are done with the stream
                byteArrayOutputStream.write(chunk, 0, bytesRead);   //read the chunk, start at 0, give me back the bytes that were read
            }
        }
        return byteArrayOutputStream.toByteArray();
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

    //if you are a existing user with appropriate permissions to add a new user
    @PostMapping(value = "/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UsernameExistsException, IOException, EmailExistsException {
        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UsernameExistsException, IOException, EmailExistsException {
        User updateUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @PostMapping(value = "/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam("profileImage") MultipartFile profileImage) throws UsernameExistsException, IOException, EmailExistsException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')") //has to authorize the user trying to delete another user to make sure they have the correct authority
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return response(HttpStatus.NO_CONTENT, USER_SUCCESSFULLY_DELETED);
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

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {  //will return a custom response for us for our delete user method
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase()), httpStatus);    //filling the requirements for our HttpResponse class inside of the constructor for the response body
    }
}
