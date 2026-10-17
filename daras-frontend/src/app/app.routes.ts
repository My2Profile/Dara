import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Home } from './home/home';
import { Dashboard } from './dashboard/dashboard';
import { Register } from './register/register';
import { Admin } from './admin/admin';
import { authGuard } from './authguard';
import { adminGuard } from './admin.guard';

export const routes: Routes = [
    
    {path: '', component: Home},
    {path: 'login', component: Login},
    {path: 'register', component: Register},
    {path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
    {path: 'admin', component: Admin, canActivate: [authGuard, adminGuard] },

];
