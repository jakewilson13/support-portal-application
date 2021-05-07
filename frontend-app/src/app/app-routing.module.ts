import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { UserComponent } from './components/user/user.component';


//configuring the routes for the application, specified with the components
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'user/management', component: UserComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' } //if it is not any of those 3 routes specified, it will fall here and forward them to the login
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],  //after we configured the routes, we pass the variable in here with routes
  exports: [RouterModule]
})
export class AppRoutingModule { }
