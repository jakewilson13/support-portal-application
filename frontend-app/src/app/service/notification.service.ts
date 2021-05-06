import { Injectable } from '@angular/core';
import { NotifierService } from 'angular-notifier';
import { NotificationType } from '../enum/notification-type.enum';

@Injectable({providedIn: 'root'})
export class NotificationService {



  constructor(private notifier: NotifierService) {}

  //the notification will contain the message and the type of notification, notification type is our enum
  public showNotification(type: NotificationType, message: string) {
    this.notifier.notify(type, message);
  }
}
