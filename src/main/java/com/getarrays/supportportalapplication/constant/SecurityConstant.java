package com.getarrays.supportportalapplication.constant;


//all of our constants we are going to use in the provider so we can generate our JWT token
//the provider is going to be what is used to generate the token and contain all of the work relating to the token
public class SecurityConstant {

    public static final long EXPIRATION_TIME = 432_000_000;     //5 days the token is valid (milliseconds)
    public static final String TOKEN_PREFIX = "Bearer ";      //whoever gives this token, doesn't need to do any further verification
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";  //we are going to use to attach the actual token to the header
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";   //whenever trying to decipher token and if we can't will provide this message
    public static final String GET_ARRAYS_LLC = "Get Arrays, LLC";  //token was provided by GetArrays(Company name)
    public static final String GET_ARRAYS_ADMINISTRATION = "User Management Portal";    //who will be using the token
    public static final String AUTHORITIES = "authorities";     //will hold all of the authorities for the users
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";    //message to the user if they are forbidden to access a resource
    public static final String ACCESS_DENIED = "You do not have permission to access this page";    //access denied
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";     //if http method is option (not get, post or anything)
    public static final String[] PUBLIC_URLS = { "/user/login", "/user/register", "/user/resetpassword/**", "/user/image/**" };   //urls allowed to be accessed without any security
//    public static final String[] PUBLIC_URLS = { "**" };
}
