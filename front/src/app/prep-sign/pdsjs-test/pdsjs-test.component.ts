import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
// @ts-ignore
import WebViewer from '@pdftron/pdfjs-express-viewer';
@Component({
  selector: 'app-pdsjs-test',
  templateUrl: './pdsjs-test.component.html',
  styleUrls: ['./pdsjs-test.component.css']
})
export class PdsjsTestComponent implements OnInit {

  @ViewChild('viewer', { static: false }) viewer: ElementRef;
  wvInstance: any;

  ngAfterViewInit(): void {

    WebViewer({
      path: '../lib',
      initialDoc: '../../assets/Hello_signed.pdf',
      licenseKey: 'UkuvU61YPrtRrF5rXByy',
    }, this.viewer.nativeElement).then(instance => {
      this.wvInstance = instance;
      // now you can access APIs through the WebViewer instance
      const {Core} = instance;

      // adding an event listener for when a document is loaded
      Core.documentViewer.addEventListener('documentLoaded', () => {
        console.log('document loaded');
        this.wvDocumentLoadedHandler();
      });

      // adding an event listener for when the page number has changed
      Core.documentViewer.addEventListener('pageNumberUpdated', (pageNumber) => {
        console.log(`Page number is: ${pageNumber}`);
      });
    });
  }

  ngOnInit() {
    this.wvDocumentLoadedHandler = this.wvDocumentLoadedHandler.bind(this);
  }

  wvDocumentLoadedHandler(): void {
    // you can access the instance object for low-level APIs
    const instance = this.wvInstance;
    const {Core, UI} = instance;
    // adds a button to the header that on click sets the page to the next page
    UI.setHeaderItems(header => {
      header.push({
        type: 'actionButton',
        img: 'https://icons.getbootstrap.com/assets/icons/caret-right-fill.svg',
        onClick: () => {
          const currentPage = Core.documentViewer.getCurrentPage();
          const totalPages = Core.documentViewer.getPageCount();
          const atLastPage = currentPage === totalPages;

          if (atLastPage) {
            Core.documentViewer.setCurrentPage(1);
          } else {
            Core.documentViewer.setCurrentPage(currentPage + 1);
          }
        }
      });
    });
  }
}
