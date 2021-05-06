import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { NotificationType } from '../enum/notification-type.enum';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';


//if you are a user trying to access a endpoint that is not allowed to you, it should reroute you to where you are authorized to be which is what a guard does

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {

constructor(private authService: AuthenticationService, private router: Router, 
  private notificationService: NotificationService){}

  
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    return this.isUserLoggedIn();
  }

  private isUserLoggedIn(): boolean {
    if(this.authService.isLoggedIn()) {
      return true;
    } else {
    this.router.navigate(['/login']);
    this.notificationService.showNotification(NotificationType.ERROR, `You need to log in to access this page`.toUpperCase());
    return false;
    }
  }
}