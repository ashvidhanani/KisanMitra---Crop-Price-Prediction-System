import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  email    = '';
  password = '';
  error    = '';
  loading  = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    this.error = '';

    if (!this.email || !this.password) {
      this.error = 'Please enter your email and password.';
      return;
    }

    this.loading = true;

    this.authService.login({ email: this.email, password: this.password })
      .subscribe({
        next: (res) => {
          this.authService.saveSession(res);
          this.router.navigate(['/predict']);
        },
        error: (err) => {
          this.error = err.error?.message || 'Login failed. Please try again.';
          this.loading = false;
        }
      });
  }
}