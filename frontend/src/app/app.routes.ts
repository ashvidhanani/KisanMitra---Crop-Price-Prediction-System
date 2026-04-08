import { Routes } from '@angular/router';
import { PredictComponent } from './components/predict/predict.component';
import { HistoryComponent } from './components/history/history.component';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '',         redirectTo: 'login',   pathMatch: 'full' },
  { path: 'login',    component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'predict',  component: PredictComponent, canActivate: [authGuard] },
  { path: 'history',  component: HistoryComponent, canActivate: [authGuard] },
  { path: '**',       redirectTo: 'login' },
];