import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AuthRequest {
  name?: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  userId?: string;
  name?: string;
  email?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;

  constructor(private readonly http: HttpClient) {}

  register(payload: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, payload);
  }

  login(payload: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, payload);
  }

  saveSession(user: AuthResponse): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  getSession(): AuthResponse | null {
    const data = localStorage.getItem('currentUser');
    return data ? JSON.parse(data) : null;
  }

  logout(): void {
    localStorage.removeItem('currentUser');
  }

  isLoggedIn(): boolean {
    return this.getSession() !== null;
  }
}