//making it an interface because we want the response to be immutable so no one can manipulate it
    //export is like public in java
export interface CustomHttpResponse {
    httpStatusCode: number;
    httpStatus: string;
    reason: string;  //tells you what happened
    message: string;    //developer message
}