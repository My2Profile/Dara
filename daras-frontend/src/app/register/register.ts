import { Component, signal } from "@angular/core";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { Auth } from "../auth";
import { sign } from "crypto";


@Component({
    selector: 'app-register',
    imports: [RouterLink, ReactiveFormsModule],
    templateUrl: './register.html',
    styleUrl: './register.scss',
})

export class Register {

    constructor (private auth: Auth) {}

    pageTitle = 'Register Page';

    isSubmitting = signal(false);
    errorMessage = signal('');
    successMessage = signal('');

    registerForm = new FormGroup({
        email: new FormControl<string>('', {
            nonNullable: true,
            validators: [Validators.email, Validators.required],
        }),
        password: new FormControl<string>('', {
            nonNullable: true,
            validators: [Validators.minLength(8), Validators.required],
        }),
    });


    submitForm() {
        if(this.registerForm.invalid) {
            return;
        }

        this.isSubmitting.set(true);
        this.errorMessage.set('');
        this.successMessage.set('');

        const {email, password} = this.registerForm.getRawValue();

        this.auth.register(email, password).subscribe({
            next: () => {
                this.isSubmitting.set(false);
                this.successMessage.set('Registration Successful - you can log in now');
                this.registerForm.reset();
            },
            error: (err) => {
                this.isSubmitting.set(false);
                if(err.status === 400) {
                    this.errorMessage.set('That email is already registered');
                } else if (err.status === 0) {
                    this.errorMessage.set('Could not connect to the backend');
                } else {
                    this.errorMessage.set('Something went wrong, please try again');
                }

            }

        })

    }

    


}

