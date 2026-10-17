import { inject } from "@angular/core";
import { CanActivateFn } from "@angular/router";
import { Auth } from "./auth";
import { Router } from "@angular/router";


export const authGuard: CanActivateFn = () => {
    const auth = inject(Auth);
    const router = inject(Router);

    if(auth.isLoggedIn()) {
        return true;
    }

    router.navigate(['/login']);
    return false;

}
