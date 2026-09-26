import { Component, signal } from '@angular/core';
import { Auth } from '../auth';
import { User } from '../models/user';
import { Observable } from 'rxjs';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {

  constructor(private auth: Auth) {}

  currentUser = signal<User | null>(null);

  loadCurrentUser() {
    this.auth.getCurrentUser().subscribe({
      next: (user) => this.currentUser.set(user),
      error: (error) => console.error('current user failed:', error),
    });
  }

  

}
