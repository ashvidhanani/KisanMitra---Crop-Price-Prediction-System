import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  name     = '';
  email    = '';
  password = '';
  error    = '';
  success  = '';
  loading  = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  register(): void {
    this.error   = '';
    this.success = '';

    if (!this.name || !this.email || !this.password) {
      this.error = 'All fields are required.';
      return;
    }

    if (this.password.length < 6) {
      this.error = 'Password must be at least 6 characters.';
      return;
    }

    this.loading = true;

    this.authService.register({
      name:     this.name,
      email:    this.email,
      password: this.password
    }).subscribe({
      next: (res) => {
        this.success = 'Account created! Redirecting to login...';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        this.error   = err.error?.message || 'Registration failed. Please try again.';
        this.loading = false;
      }
    });
  }
}