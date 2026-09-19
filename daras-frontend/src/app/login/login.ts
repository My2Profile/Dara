import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  pageTitle = 'Login Page'

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.email,Validators.required]),
    password: new FormControl('', [Validators.minLength(8), Validators.required])
  })

  submitForm() {
    console.log(this.loginForm.value);
    console.log(this.loginForm.get('email')?.errors);
    console.log(this.loginForm.valid);
  }
}


