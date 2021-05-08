import { HttpErrorResponse} from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';  //subscription lets you be notified whenever the response comes back
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {

  public showLoading: boolean;  //boolean by default is false
  private subscriptions: Subscription[] = []; //adding all of our subscription into this array then removing them on destroy

  constructor(private router: Router, private authService: AuthenticationService, 
    private notificationService: NotificationService) { }





    //if a user is already logged in and tries to navigate to /register
  ngOnInit(): void {
    if(this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/user/management');
    } 
  }

  public onRegister(user: User): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.authService.register(user).subscribe(  //going to return a user
          (response: User) => {
            this.showLoading = false;
            this.sendNotification(NotificationType.SUCCESS, `A new account was created for ${response.firstName}.
            Please check your email for password to login.`);
          },
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
            this.showLoading = false;
        }
      )
    )
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if(message != null) { //if the error exists we send the message to the user
      this.notificationService.showNotification(notificationType, message);
    } else {
      this.notificationService.showNotification(notificationType, 'An error occured. Please try again.');  //we don't know what happened if the error occured so we are telling them to try again
    }
  }

  ngOnDestroy(): void {
  }

}