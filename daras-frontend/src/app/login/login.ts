import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {Auth} from "../auth";
import { User } from '../models/user';

@Component({
  selector: 'app-login',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {

  constructor(private auth: Auth, private router: Router) {}

  currentUser = signal<User | null>(null);

  pageTitle = 'Login Page'

  isSubmitting = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  loginForm = new FormGroup({
    email: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.email, Validators.required]
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.minLength(8), Validators.required]
    })
  })

  submitForm() {

    if(this.loginForm.invalid) {
      return;
    }

    console.time('login-request');

    this.isSubmitting.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const { email, password } = this.loginForm.getRawValue();
    this.auth.login(email, password).subscribe({
      next: (response) => {
        console.log('success-callback');
        console.timeEnd('login-request');
        this.auth.storeTokens(response);
        this.isSubmitting.set(false);
        this.errorMessage.set('');
        this.successMessage.set('Login Successful');
        this.router.navigate(['/dashboard']);
      }, 
      
      
      error: (err) => {
        console.log('error-callback');
        console.timeEnd('login-request');
        this.errorMessage.set('');
        this.successMessage.set('');
        this.isSubmitting.set(false);
        if(err.status === 401) {
          this.errorMessage.set('Email or password is incorrect');
        } else if(err.status === 0) {
          this.errorMessage.set('Could not connect to the backend');
        } else {
          this.errorMessage.set('Something went wrong please try again');
        }

      },  

    });
    
  }

}



