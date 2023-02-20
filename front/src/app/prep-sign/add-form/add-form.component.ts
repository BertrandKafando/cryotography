import { Component, OnInit } from '@angular/core';
import { pdfDefaultOptions } from 'ngx-extended-pdf-viewer';
import { PrepSignService } from '../services/prep-sign.service';

@Component({
  selector: 'app-add-form',
  templateUrl: './add-form.component.html',
  styleUrls: ['./add-form.component.css']
})
export class AddFormComponent implements OnInit {

  pdfSrc="../../assets/Hello2.pdf";
  testSrc: any;

  constructor(
    private prepSignService: PrepSignService,
  ) {
    pdfDefaultOptions.assetsFolder = 'bleeding-edge';
   }

  ngOnInit(): void {
    this.prepSignService.loadTestPdf().subscribe((data) => {
      this.testSrc = data;
    console.log(this.testSrc.getPage(1));
    });

  }
/*
  loadComplete(pdf: any): void {
    for (let i = 1; i <= pdf.numPages; i++) {

        // track the current page
        let currentPage = null;
        pdf.getPage(i).then(p => {
            currentPage = p;

            // get the annotations of the current page
            return p.getAnnotations();
        }).then(ann => {

            // ugly cast due to missing typescript definitions
            // please contribute to complete @types/pdfjs-dist
            const annotations = (<any>ann) as PDFAnnotationData[];

            annotations
                .filter(a => a.subtype === 'Widget') // get the form field annotation only
                .forEach(a => {

                    // get the rectangle that represent the single field
                    // and resize it according to the current DPI
                    const fieldRect = currentPage.getViewport(this.dpiRatio)
                                                 .convertToViewportRectangle(a.rect);

                    // add the corresponding input
                    this.addInput(a, fieldRect);
                });
        });
    }
} */

}
