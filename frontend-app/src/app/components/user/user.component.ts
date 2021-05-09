import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { User } from 'src/app/model/user';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    // behavior subject is a special type of observable so you can subscribe to messages like any other observable. 
    //The unique features of BehaviorSubject are at any point, you can retrieve the last value of the subject in a non-observable code using the getValue() method.

  private titleSubject = new BehaviorSubject<string>('Users');  //by default whenever this page loads, the title is going to be users
  public titleAction$ = this.titleSubject.asObservable(); //this is the listener for the titleSubject so whenever it changes we can update it's value
  public users: User[]; //will hold all of the users we have
  public user: User;  //specific user
  public refreshing: boolean; //default it's false
  public selectedUser: User;
  private subscriptions: Subscription[] = [];
  

  constructor(private userService: UserService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.getUsers(true);
  }


  public changeTitle(title: string): void {
    this.titleSubject.next(title);  //this is how we change the value of the title, call next and pass in the title
  }

  public getUsers(showNotification: boolean): void {  //before we load all of the users
    this.refreshing = true; //determines that the application is fetching data
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        (response: User[]) => { //after we load all of the users
          this.userService.addUsersToLocalCache(response);
          this.users = response;  //setting the response to the array of users we instantiated so we can use it in the user html
          this.refreshing = false;
          if(showNotification) {  //if we get a good response after refreshing the users
            this.sendNotification(NotificationType.SUCCESS, `${response.length} user(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message); //accessing the actual error & the actual message FROM HttpErrorResponse
          this.refreshing = false;
        }
      )
    );
  }

  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    document.getElementById('openUserInfo').click();  //getting the id of the button to open the users information
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if(message) { //if the error exists we send the message to the user(if it's true)
      this.notificationService.showNotification(notificationType, message);
    } else {
      this.notificationService.showNotification(notificationType, 'An error occured. Please try again.');  //we don't know what happened if the error occured so we are telling them to try again
    }
  }
}
