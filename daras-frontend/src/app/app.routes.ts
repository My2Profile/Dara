import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Home } from './home/home';
import { Dashboard } from './dashboard/dashboard';

export const routes: Routes = [
    
    {
        path: '',
        component: Home,
    },
    
    {
        path: 'login',
        component: Login,
    },

    {
        path: 'dashboard',
        component: Dashboard,
    }

];
