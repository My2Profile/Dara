import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  pageTitle = 'Home page';

  clickCount = 0;

  increaseCount() {
    this.clickCount++;
  }

  resetCount() {
    this.clickCount = 0;
  }

}
