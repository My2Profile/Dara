import { inject } from "@angular/core";
import { CanActivateChildFn, Router } from "@angular/router";
import { Auth } from "./auth";

export const adminGuard: CanActivateChildFn = () => {
    const auth = inject(Auth);
    const router = inject(Router);

    if(auth.isLoggedIn() && auth.getRole() === 'ADMIN') {
        return true;
    }

    router.navigate(['/dashboard']);
    return false;

}