import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/user.service';
import { AuthenticationInterceptor } from './interceptor/authentication.interceptor';
import { AuthenticationGuard } from './guard/authentication.guard';
import { NotificationModule } from './notification.module';
import { NotificationService } from './service/notification.service';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NotificationModule,
  ],
  //you wire services inside of the application inside of the providers array
  providers: [AuthenticationGuard, AuthenticationService, UserService, NotificationService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthenticationInterceptor, multi: true}], //multi allows us to create multiple different instances of the interceptor inside of the injector
  bootstrap: [AppComponent]
})

export class AppModule {}