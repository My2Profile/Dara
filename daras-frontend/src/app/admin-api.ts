import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { User } from "./models/user";

@Injectable({
    providedIn: 'root',
})

export class AdminApi {

    private readonly apiUrl = 'http://localhost:8080/api/admin';

    constructor(private http: HttpClient) {}

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiUrl}/getAll`);
    }

    getUserById(id: number): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/users/${id}`);
    }

    changeRole(id: number, role: string): Observable<string> {
        return this.http.patch(`${this.apiUrl}/users/${id}/role`, { role }, { responseType: 'text' });
    }

    changeStatus(id: number, state: boolean): Observable<string> {
        return this.http.patch(`${this.apiUrl}/users/${id}/active`, { state }, { responseType: 'text' });
    }

    blockUser(id: number, reason: string): Observable<string> {
        return this.http.patch(`${this.apiUrl}/users/${id}/block`, { reason }, { responseType: 'text' });
    }

    unblockUser(id: number): Observable<string> {
        return this.http.patch(`${this.apiUrl}/users/${id}/unblock`, {}, { responseType: 'text' });
    }

    softDeleteUser(id: number): Observable<string> {
        return this.http.delete(`${this.apiUrl}/users/${id}/delete`, { responseType: 'text' });
    }

    restoreUser(id: number): Observable<string> {
        return this.http.patch(`${this.apiUrl}/users/${id}/restore`, {}, { responseType: 'text' });
    }

}
