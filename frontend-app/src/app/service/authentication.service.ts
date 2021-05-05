import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { JwtHelperService } from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})

export class AuthenticationService {

  private host: string = environment.apiUrl;  //host is localhost8081
  private token: string;
  private loggedInUsername: string;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  //observables are when you have code that could take some time to execute (when you make http requests)
    //so run this piece of code, and let me know when you get the response back
      //the observable will return an http response of any type or an error
  public login(user: User): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http.post<HttpResponse<any> | HttpErrorResponse>
    (`${this.host}/user/login`, user, {observe: 'response'}); //after the post request is executed it returns the url, the object (which is user) to the request body, then passes us back the entire response (headers, etc. we need to get the jwt token)
  }

  //only going to return a user or an http error response
  public register(user: User): Observable<User | HttpErrorResponse> {
    return this.http.post<User | HttpErrorResponse>
    (`${this.host}/user/register`, user); //only going to give us the response body (the user)
  }

  //for when a user logs out we went to clear all of the local storage and set the token to null because we don't need it anymore
    // local storage is a Web Storage API interface that provides access to a particular domain's session or local storage. It allows, for example, the addition, modification, or deletion of stored data items.
  public logout(): void {
    this.token = null;
    this.loggedInUsername = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('users');
  }

  public saveToken(token: string): void {
    this.token = token; //wanting to save the token to the local storage after they login so we can reference it any time a user is making calls to the backend
    localStorage.setItem('token', token); //set the token and reference it by 'token' (like key & value)
  }

  public addUserToLocalCache(user: User): void {
    localStorage.setItem('user', JSON.stringify(user)); //local storage only accepts a string, so we use JSON.stringify to set the user to a string
  }

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user')); //parse is opposite of stringify, it takes a string and parses it into an object
  }

  public loadToken(): void {
    this.token = localStorage.getItem('token');
  }

  public getToken(): string {
    return this.token;
  }

    //checking to see if a user is logged in or not
  public isLoggedIn(): boolean {
    
    this.loadToken();
    if(this.token != null && this.token !== '') { //if the token isn't null and it's not empty
      if(this.jwtHelper.decodeToken(this.token).sub != null || ''){  //decode the token and then get the subject(the username), if we actually have a username that isn't null and it's not empty
        if(!this.jwtHelper.isTokenExpired(this.token)) {  //if token is not expired
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub; //then we set the loggedInUsername from the token
          return true; 
        }
      }
    } else {  //if the token doesn't exist or it's empty
      this.logout();
      return false;
    }
  }
}