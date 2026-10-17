import { DatePipe } from "@angular/common";
import { Component, OnInit, signal } from "@angular/core";
import { AdminApi } from "../admin-api";
import { User } from "../models/user";


@Component({
    selector: 'app-admin',
    imports: [DatePipe],
    templateUrl: './admin.html',
    styleUrl: './admin.scss',
})


export class Admin implements OnInit {
    
    constructor(private adminApi: AdminApi) {}

    users = signal<User[]>([]);
    errorMessage = signal('');
    message = signal('');

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers() {
        this.adminApi.getAllUsers().subscribe({
            next: (users) => this.users.set([...users].sort((a, b) => a.id - b.id)),
            error: () => this.errorMessage.set('Could not load the users (are you indeed an admin?)'),
        })
    }

    changeRole(userId: number, role: string) {
        this.adminApi.changeRole(userId, role).subscribe({
            next: () => {
                this.message.set('Role updated');
                this.loadUsers();
            },
            error: () => this.errorMessage.set('Could not change the role'),
        });
    }

    toggleActive(userId: number, currentlyActive: boolean) {
        this.adminApi.changeStatus(userId, !currentlyActive).subscribe({
            next: () => {
                this.message.set('Status updated');
                this.loadUsers();
            },
            error: () => this.errorMessage.set('Could not change the status'),
        });
    }


    blockUser(userId: number) {
        const reason = prompt('Reason for blocking this user?');
        if(!reason) return;
        this.adminApi.blockUser(userId, reason).subscribe({
            next: () => {
                this.message.set('User blocked');
                this.loadUsers();
            },
            error: () => this.errorMessage.set('Could not block the user'),
        });
    }

    unblockUser(userId: number) {
        this.adminApi.unblockUser(userId).subscribe({
            next: () => {
                this.message.set('User unblocked');
                this.loadUsers();
            },
            error: () => this.errorMessage.set('Could not unblock the user'),
        });
    }

    softDelete(userId: number) {
    if (!confirm('Soft-delete this user?')) return;
    this.adminApi.softDeleteUser(userId).subscribe({
      next: () => {
        this.message.set('User deleted');
        this.loadUsers();
      },
      error: () => this.errorMessage.set('Could not delete user (cannot delete yourself or the last admin)'),
    });
  }

  restoreUser(userId: number) {
    this.adminApi.restoreUser(userId).subscribe({
      next: () => {
        this.message.set('User restored');
        this.loadUsers();
      },
      error: () => this.errorMessage.set('Could not restore user'),
    });
  }
    

}
