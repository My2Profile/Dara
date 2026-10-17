import { Component, OnInit, signal } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { RouterLink, Router } from "@angular/router";
import { Auth } from "../auth";
import { sign } from "crypto";
import { SessionResponse } from "../models/session-response";
import { error } from "console";
import { User } from "../models/user";

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})

export class Dashboard implements OnInit {

  constructor(
    private auth: Auth,
    private router: Router,
  ) {}

  currentUser = signal<User | null>(null);
  sessions = signal<SessionResponse[]>([]);

  message = signal('');
  errorMessage = signal('');

  emailForm = new FormGroup({
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.email, Validators.required],
    }),
  });

  passwordForm = new FormGroup({
    oldPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    newPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.minLength(8), Validators.required],
    })
  });

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadSessions();
  }

  loadCurrentUser() {
    this.auth.getCurrentUser().subscribe({
      next: (user) => this.currentUser.set(user),
      error: (error) => console.error('current user failed', error),
    })
  }

  loadSessions() {
    this.auth.getSessions().subscribe({
      next: (sessions) => this.sessions.set(sessions),
      error: (error) => console.error('sessions failed:', error),
    })
  }


  submitEmail() {
    if(this.emailForm.invalid) return;

    this.errorMessage.set('');
    this.message.set('');

    const {email} = this.emailForm.getRawValue();
    this.auth.updateEmail(email).subscribe({
      next: () => {
        this.message.set('Email updated - please log in again');
        this.auth.clearTokens();
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },

      error: () => this.errorMessage.set('Could not update the email (it may already be taken)'),
    });

  }

  submitPassword() {
    if(this.passwordForm.invalid) return;

    this.errorMessage.set('');
    this.message.set('');

    const {oldPassword, newPassword} = this.passwordForm.getRawValue();

    this.auth.changePassword(oldPassword, newPassword).subscribe({
      next: () => {
        this.message.set('Password updated successfully');
        this.passwordForm.reset();
      },
      error: () => this.errorMessage.set('Old password is incorrect'),
    });

  }

  revokeSession(sessionId: number) {
    this.auth.revokeSession(sessionId).subscribe({
      next: () => this.loadSessions(),
      error: () => this.errorMessage.set('Could not revoke that session'),
    });

  }

  logout() {
    const refreshToken = this.auth.getRefreshToken();
    if (!refreshToken) {
      this.finishLogout();
      return;
    }
    this.auth.logout(refreshToken).subscribe({
      next: () => this.finishLogout(),
      error: () => this.finishLogout(),
    });
  }

  logoutAll() {
    this.auth.logoutAll().subscribe({
      next: () => this.finishLogout(),
      error: () => this.finishLogout(),
    });
  }

  private finishLogout() {
    this.auth.clearTokens();
    this.router.navigate(['/login']);
  }


}
