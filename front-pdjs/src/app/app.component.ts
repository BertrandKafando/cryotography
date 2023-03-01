import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'front-pdjs';

  constructor() {

  }

  beforePrint(event: any) {
    console.log("beforePrint");
    console.log(event);
  }
  createdSigningForm($event: any) {
    console.log("createdSigningForm");
    console.log($event);
  }
  onSign($event:any) {
    console.log("onSign");
    console.log($event);
  }
  public testPagesLoaded(count: number) {
    console.log("testPagesLoaded() successfully called. Total pages # : " + count);
    alert(`Document is loaded!. Total pages : ${count}`);
  }
  @Output() pageSelected = new EventEmitter<number>();

  // ...

  onPageSelected(event:any) {
    this.pageSelected.emit(event.pageNumber);
  }
}
