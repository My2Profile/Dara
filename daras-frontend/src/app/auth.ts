import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResponse } from './models/auth-response';
import { Observable } from 'rxjs';
import { User } from './models/user';
import { SessionResponse } from './models/session-response';

@Injectable({
  providedIn: 'root',
})


export class Auth {
  
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  register(email: string, password: string): Observable<string> {

    return this.http.post(`${this.apiUrl}/auth/register`, {email, password}, {responseType: 'text'});
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, {email, password});
  }

  refresh(refreshToken: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/refresh`, {refreshToken});
  }

  logout(refreshToken: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/auth/logout`, {refreshToken}, {responseType: 'text'});
  }

  logoutAll(): Observable<string> {
    return this.http.post(`${this.apiUrl}/auth/logout-all`, {}, {responseType: 'text'});
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/me`);
  }

  updateEmail(email: string): Observable<string> {
    return this.http.patch(`${this.apiUrl}/users/me/email`, {email}, {responseType: 'text'});
  }

  changePassword(oldPassword: string, newPassword: string): Observable<string> {
    return this.http.patch(
      `${this.apiUrl}/users/me/password`,
      { oldPassword, newPassword },
      { responseType: 'text' },
    );
  }

  getSessions(): Observable<SessionResponse[]> {
    return this.http.get<SessionResponse[]>(`${this.apiUrl}/users/me/sessions`, {});
  }

  revokeSession(sessionId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/users/me/sessions/${sessionId}`, {responseType: 'text'});
  }

  storeTokens(response: AuthResponse) {
    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('refreshToken', response.refreshToken);
  }

  clearTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  getAccessToken(): string | null {
    return typeof localStorage === 'undefined' ? null : localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return typeof localStorage === 'undefined' ? null : localStorage.getItem('refreshToken');
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  getRole(): string | null {
    const claims = this.decodeToken(this.getAccessToken());
    return (claims?.['role'] as string | undefined)?? null;
  }

  getEmail(): string | null {
    const claims = this.decodeToken(this.getAccessToken());
    return (claims?.['sub'] as string | undefined) ?? null;
  }

  private decodeToken(token: string | null): Record<string, unknown> | null {
    if(!token) { return null; }

    try {
      const payload = token.split('.')[1];
      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(normalized));
    } catch {
      return null;
    }

  }

}

