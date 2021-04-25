package com.getarrays.supportportalapplication.model;

import org.springframework.http.HttpStatus;

//creating this class so we can have a uniform way to give a response to a user,
// a way to make the api consistent throughout the application
//without it and an error occurs it will provide you a very long error message that can expose the internal workings of the application
public class HttpResponse {

    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String reason;  //tells you what happened
    private String message;    //developer message

    //EXAMPLE: {
    // httpStatusCode: 404,
    // httpStatus: "NOT_FOUND"
    // reason: "Not Found",
    // message: "The resource you are trying to access is not found"
    // }

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "httpStatusCode=" + httpStatusCode +
                ", httpStatus=" + httpStatus +
                ", reason='" + reason + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
