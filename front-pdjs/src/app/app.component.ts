import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'front-pdjs';

  constructor() {

  }
  onSign() {
    console.log("sign");
  }
  beforePrint(event: any) {
    console.log("beforePrint");
    console.log(event);
  }
  createdSigningForm($event: any) {
    console.log("createdSigningForm");
    console.log($event);
  }
}
