import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Auth } from './auth';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  
  const auth = inject(Auth);
  const router = inject(Router);

  const accessToken = auth.getAccessToken();
  const authReq = accessToken ? 
  req.clone( {setHeaders: {Authorization: `Bearer ${accessToken}`}} ) : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthEndpoint = req.url.includes('/api/auth');

      if(error.status !== 401 || isAuthEndpoint) {
        return throwError(() => error);
      }

      const refreshToken = auth.getRefreshToken();
      if(!refreshToken) {
        auth.clearTokens();
        router.navigate(['/login']);
        return throwError(() => error);
      }

      return auth.refresh(refreshToken).pipe(
        switchMap((response) => {
          auth.storeTokens(response);
          const retryReq = req.clone({
            setHeaders: {Authorization: `Bearer ${response.accessToken}` },
          });
          return next(retryReq);

        }),

        catchError((refreshError) => {
          auth.clearTokens();
          router.navigate(['/login']);
          return throwError(() => refreshError);
        })
      )

    })
  )


};
