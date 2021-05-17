import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../service/authentication.service';


//interceptors are a way to intercept HTTP requests and responses to transform or handle them before passing them along
  //intercepts and handles an HttpRequest or HttpResponse

  //injectable lets us know it's a service
@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {}

  //checking to see if the url that the request has contains localhost:8081/user/login, /user/register, /user/resetpassword
    //if it does then we don't need to do anything so we just let the request still take it's course because we do not need to give jwt token
  intercept(httpRequest: HttpRequest<any>, httpHandler: HttpHandler): Observable<HttpEvent<any>> {
    if(httpRequest.url.includes(`${this.authenticationService.host}/user/login`)) {
      return httpHandler.handle(httpRequest);
    }
    if(httpRequest.url.includes(`${this.authenticationService.host}/user/register`)) {
      return httpHandler.handle(httpRequest);
    }
    //for any other route than those 3, we want to make sure we are sending the request properly
    //if the user has logged in before and the token is saved in the local storage
    this.authenticationService.loadToken(); //takes the token from the local storage and adds it to the variable "token" in authentication.service
    const token = this.authenticationService.getToken(); //once the token is loaded we can access it
    //cloning the request because the request itself is immutable(httpRequest)
    const request = httpRequest.clone({setHeaders: { Authorization: `Bearer ${token}`}});  //putting bearer in front of the actual token and passing Authorization because that is the header we want
    return httpHandler.handle(request); //then we pass the clone to the handle method so it can pass the request to the next interceptors until the request gets to the destination which is the server
  }
}
