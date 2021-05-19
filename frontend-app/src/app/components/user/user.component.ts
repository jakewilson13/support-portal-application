import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Role } from 'src/app/enum/role.enum';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { FileUploadStatus } from 'src/app/model/file-upload.status';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';
import { SubSink } from 'subsink';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, OnDestroy {

    // behavior subject is a special type of observable so you can subscribe to messages like any other observable. 
    //The unique features of BehaviorSubject are at any point, you can retrieve the last value of the subject in a non-observable code using the getValue() method.

  private subs = new SubSink();
  private titleSubject = new BehaviorSubject<string>('Users');  //by default whenever this page loads, the title is going to be users
  public titleAction$ = this.titleSubject.asObservable(); //this is the listener for the titleSubject so whenever it changes we can update it's value
  public users: User[]; //will hold all of the users we have
  public user: User;  //specific user to represent the logged in user
  public refreshing: boolean; //default it's false
  public selectedUser: User;
  private subscriptions: Subscription[] = [];
  public fileName: string;
  public profileImage: File;
  public editUser = new User(); //inside of the user class we created a constructor with default values which we utilize here
  private currentUsername: string;
  public fileStatus = new FileUploadStatus();
  

  constructor(private userService: UserService, private notificationService: NotificationService, 
    private authenticationService: AuthenticationService, private router: Router) {}

  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache(); //getting the logged in user from the cache
    this.getUsers(true);
  }


  public changeTitle(title: string): void {
    this.titleSubject.next(title);  //this is how we change the value of the title, call next and pass in the title
  }

  public getUsers(showNotification: boolean): void {  //before we load all of the users
    this.refreshing = true; //determines that the application is fetching data
    this.subs.add(
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
      ));
  }

  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    this.clickButton('openUserInfo');
  }

  //getting the fileName which is a string and then we are getting the actual file
  public onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName = fileName;
    this.profileImage = profileImage;
  }

  public saveNewUser(): void {
    //the click will click the real save that is on the button
    this.clickButton('new-user-save');
  }

  public onAddNewUser(userForm: NgForm): void {
      //we set null because they don't have a logged in username, getting all of the form data of the user, and getting the profile image,
      //since it's a call to the backend we have to subscribe to it because it's an observable
    const formData = this.userService.createUserFormData(null, userForm.value, this.profileImage);
  
      this.subs.add(
        this.userService.addUser(formData).subscribe(
        (response: User) => {
          //it will click the button and close the actual modal once the user is successful
          this.clickButton('new-user-close');
          //setting false because we aren't going to show the notification that's consisted of the getUsers method
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          userForm.reset();
          this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} updated successfully.`)
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null; //don't want to keep the filename there if something goes wrong or exit out of the form
        }
    ));
  }

  public onUpdateUser(): void {
    const formData = this.userService.createUserFormData(this.currentUsername, this.editUser, this.profileImage);
      this.subs.add(
        this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.clickButton('closeEditUserModalButton');
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
         this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} updated successfully.`); 
        }, 
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
        }
      )
    );
  }

  public searchUsers(searchName: string): void {
    const results: User[] = [];
    for(const user of this.userService.getUsersFromLocalCache()) {  //looping through all of the users inside of the local cache
        if(user.firstName.toLowerCase().indexOf(searchName.toLowerCase()) !== -1 || 
        user.lastName.toLowerCase().indexOf(searchName.toLowerCase()) !== -1 || 
        user.username.toLowerCase().indexOf(searchName.toLowerCase()) !== -1 ||
        user.userId.toLowerCase().indexOf(searchName.toLowerCase()) !== -1) { //indexOf() returns the first index at which a given element that can be found inside of an array. It returns -1 if it's not present which is why we are doing the check
          results.push(user);
        }
    } //now we are done looping we can set those users to the array we made
    this.users = results;
    if(results.length == 0 || !searchName) {  //after they are done searching we want to clear the results and put all of the users back. If we didn't put anything inside of the searchName or we deleted it
      this.users = this.userService.getUsersFromLocalCache(); //puts all of the users back
    }   
  }

  public onUpdateCurrentUser(user: User): void {
    this.refreshing = true;
    this.currentUsername = this.authenticationService.getUserFromLocalCache().username;
    const formData = this.userService.createUserFormData(this.currentUsername, user, this.profileImage);
      this.subs.add(
        this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
         this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} updated successfully.`); 
        }, 
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.profileImage = null;
          this.refreshing = false;
        }
      )
    );
  }

  public onLogOut(): void {
    this.authenticationService.logout();
    this.router.navigateByUrl('/login');
    this.sendNotification(NotificationType.SUCCESS, `You've been successfully logged out.`);
  }

  public onUpdateProfileImage(): void {
    const formData = new FormData();
    formData.append('username', this.user.username);  //formData takes a key and the value, this will take in the current username
    formData.append('profileImage', this.profileImage); //this will take in the profile image that they are updating. For the key, we have to match it up with what we set up in the backend so the data can reflect accordingly.
    this.subs.add(
      this.userService.updateProfileImage(formData).subscribe(
        (event: HttpEvent<any>) => {
          this.reportUploadProgress(event);
         
        }, 
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.fileStatus.status = 'done';  //if there is an error we set the progress bar to done so the progress bar will dissappear 
        }
      )
    );
  }

  public updateProfileImage(): void {
    this.clickButton('profile-image-input');
  }

  public onResetPassword(emailForm: NgForm): void { //since we are reseting the password we want to be able to reset the actual form
    this.refreshing = true;
    const emailAddress = emailForm.value['reset-password-email']; //passing in the name of the input so we can grab the value for that specific property on the object
    this.subs.add(
      this.userService.resetPassword(emailAddress).subscribe(
        (response: CustomHttpResponse) => { //the process could execute successfully
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (error: HttpErrorResponse) => { //an error could happen while they try to reset the password
          this.sendNotification(NotificationType.WARNING, error.error.message);
          this.refreshing = false;
        },
        () => { //resetting the form
          emailForm.reset();
        }
      )
    );
  }

public onDeleteUser(username: string): void {
  this.subs.add(
    this.userService.deleteUser(username).subscribe(
      (response: CustomHttpResponse) => {
        this.sendNotification(NotificationType.SUCCESS, response.message);  //grabs our response from the backend
        this.getUsers(false); //not going to reload afters after deletion and display the notification
      }, 
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
      }
    )
  );
}

  public onEditUser(editUser: User): void {
    this.editUser = editUser; //get the user and set that user to the user in the class
    this.currentUsername = editUser.username; //getting ahold of the username before they get a chance to make any edit
    this.clickButton('openUserEdit'); //then we open the modal that they need to open to make the edits
  }

  //in typescript to create a getter like java, just use the keyword get
    //will reference the getters inside of the UI to give permissions what what different users can see
  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  public get isManager(): boolean {
    return this.isAdmin || this.getUserRole() === Role.MANAGER;
  }

  public get isAdminOrManager(): boolean {
    return this.isAdmin || this.isManager;
  }

   private getUserRole(): string {  //gives us the actual role from the user
    return this.authenticationService.getUserFromLocalCache().role;
   }

  //we want to send some information back to the front end in real time.
    //while this is sending reports of the file being uploaded we want to update the front-end so we can see the progress
  reportUploadProgress(event: HttpEvent<any>): void {
    switch(event.type) {  //switching on the different event types
        case HttpEventType.UploadProgress: 
          this.fileStatus.percentage = Math.round(100 * event.loaded / event.total);  //multiplying by 100 so it can be an actual pertentage without the decmials so we round it up, the amount uploaded divided by the total so we can get the percentage
          this.fileStatus.status = 'progress';  //will use this in html. will only show the progress bar if it's equal to progress
          break;
        case HttpEventType.Response:
          if(event.status === 200) {
            this.user.profileImageUrl = `${event.body.profileImageUrl}?time=${new Date().getTime()}`; //since we set the time in miliseconds for the image everytime an image is chagned, it sets the miliseconds of when it was changed to the url of the image. That way it forces the browser to re-fetch the image and the image automatically populates when we select a new one.
            this.sendNotification(NotificationType.SUCCESS, `${event.body.firstName}\'s profile image updated successfully.`); 
            this.fileStatus.status = 'done';
            break;
          } else {
            this.sendNotification(NotificationType.ERROR, `Unable to upload image. Please try again.`);
            break;
          }
        default: 
        `Finished all processes.`;
    }              
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if(message) { //if the error exists we send the message to the user(if it's true)
      this.notificationService.showNotification(notificationType, message);
    } else {
      this.notificationService.showNotification(notificationType, 'An error occured. Please try again.');  //we don't know what happened if the error occured so we are telling them to try again
    }
  }

  //takes in the button id and calls the click function to actually execute the method
  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  ngOnDestroy(): void { //unsubscribing from all of the calls that we are making to the backend
    this.subs.unsubscribe();
  }
}
