import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';  //subscription lets you be notified whenever the response comes back
import { HeaderType } from 'src/app/enum/header-type.enum';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  public showLoading: boolean;  //boolean by default is false
  private subscriptions: Subscription[] = []; //adding all of our subscription into this array then removing them on destroy

  constructor(private router: Router, private authService: AuthenticationService, 
              private notificationService: NotificationService) {}



  ngOnInit(): void {
    if(this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/user/management');  //if you're logged in it will always send you here if you try to go to login page.(its the default)
    } else {
      this.router.navigateByUrl('/login');  //if you're NOT logged in it will always send you here
    }
  }

  public onLogin(user: User): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.authService.login(user).subscribe( //making a call to the backend to get the user to login
      (response: HttpResponse<User>) => { //if we get a successful response back from the backend

        //inside of our post mapping inside of the backend we have a "getJwtHeader" method. That method takes in the name of the actual header & the value. 
        // the name of the actual header that we set is "Jwt-Token" which is why we reference it here to get the token.

        const token = response.headers.get(HeaderType.JWT_TOKEN);  //going to grab the token from the headers, only takes in the header name(the key)
        this.authService.saveToken(token);                //saving the token inside of the local storage inside of the browser
        this.authService.addUserToLocalCache(response.body);  // once we save the token we can add the user to the cache and pass in the body because the body holds the user object
        this.router.navigateByUrl('/user/management');
        this.showLoading = false;
      },
      //if there is an error
      (errorResponse: HttpErrorResponse) => {
        console.log(errorResponse);
        this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message);  //sending the error message to the user and accessing the error message inside of the response
        this.showLoading = false;
      }
    )
  );
}   //error message could be null so we are checking that here
  private sendErrorNotification(notificationType: NotificationType, message: string) {
    if(message != null) { //if the error exists we send the message to the user
      this.notificationService.showNotification(notificationType, message);
    } else {
      this.notificationService.showNotification(notificationType, 'AN ERROR OCCURED. PLEASE TRY AGAIN');  //we don't know what happened if the error occured so we are telling them to try again
    }
  }

  //un-subscribing after logins get called that way we don't have memory leaks
  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe()); //using forEach to go through the array, and unsubscribing to the information
  }
}
