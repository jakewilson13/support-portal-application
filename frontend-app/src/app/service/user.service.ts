import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { CustomHttpResponse } from '../model/custom-http-response';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private host: string = environment.apiUrl;  //host is localhost8081


  constructor(private http: HttpClient) {}

  public getUsers(): Observable <User[] | HttpErrorResponse> {
    return this.http.get<User[]>(`${this.host}/user/list`);
  }

  //since using @RequestParam for adding a user inside of our backend, when we tested with postman we had to add a user using "form-data" which is why we are using it here 
  public addUser(formData: FormData): Observable <User | HttpErrorResponse> {
    return this.http.post<User>(`${this.host}/user/add`, formData);
  }

  public updateUser(formData: FormData): Observable <User | HttpErrorResponse> {
    return this.http.post<User>(`${this.host}/user/update`, formData);
  }

  public resetPassword(email: string): Observable <CustomHttpResponse | HttpErrorResponse> {
    return this.http.get<CustomHttpResponse>(`${this.host}/user/resetpassword/${email}`);
  }

  //the reason it will be httpevent because it will track the upload progress
  public updateProfileImage(formData: FormData): Observable <HttpEvent<User> | HttpErrorResponse> {
    return this.http.post<User>(`${this.host}/user/updateProfileImage`, formData, 
    {reportProgress: true,  //reporting the upload progress
      observe: 'events' //observe all of the events that are happening before we get the response
    });
  }

  public deleteUser(userId: number): Observable <CustomHttpResponse | HttpErrorResponse> {
    return this.http.delete<CustomHttpResponse>(`${this.host}/user/delete/${userId}`);
  }

  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users)); //it takes an array of users so we have to convert it
  }

  public getUsersFromLocalCache(): User[] {
     if(localStorage.getItem('users')) {
      return JSON.parse (localStorage.getItem('users'));
    }
    return null;
  }

  public createUserFormData(loggedInUsername: string, user: User, profileImage: File): FormData {
    const formData = new FormData();
    formData.append('currentUsername', loggedInUsername);  //.append is how we insert the "key, value" inside of the form data
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('role', user.role);
    formData.append('profileImage', profileImage);
    formData.append('isActive', JSON.stringify(user.active)); //inside of our update method inside of the backend, it is expecting isActive & isNotLocked as a string
    formData.append('isNonLocked', JSON.stringify(user.notLocked));
    return formData;
  }
}