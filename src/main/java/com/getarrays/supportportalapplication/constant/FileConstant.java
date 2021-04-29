package com.getarrays.supportportalapplication.constant;

public class FileConstant {

    public static final String USER_IMAGE_PATH = "/user/image/";
    public static final String JPG_EXTENSION = "jpg";
    public static final String USER_FOLDER = System.getProperty("user.home") + "/supportportal/user/";  //whatever the user home is on the system(if it is linux it will find the home, windows, etc.), every time the application starts we want to create this
    public static final String DIRECTORY_CREATED = "Created directory for: ";
    public static final String DEFAULT_USER_IMAGE_PATH = "/user/image/profile/";
    public static final String FILE_SAVED_IN_FILE_SYSTEM = "Saved file in the file system by name: ";
    public static final String DOT = ".";   //defines the seperation between the file name and the extension
    public static final String FORWARD_SLASH = "/";
    public static final String TEMP_PROFILE_IMAGE_BASE_URL = "https://robohash.org";    //used to generate a random picture for a user if they did not provide one

}
