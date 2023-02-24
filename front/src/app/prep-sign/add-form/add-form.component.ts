import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { PDFDocumentProxy } from 'ng2-pdf-viewer/src/app/pdf-viewer/pdf-viewer.module';
import { PrepSignModel } from '../model/prep-sign.model';
import { PrepSignService } from '../services/prep-sign.service';
import * as pdfjsLib from "pdfjs-dist";

@Component({
  selector: 'app-add-form',
  templateUrl: './add-form.component.html',
  styleUrls: ['./add-form.component.css']
})
export class AddFormComponent implements OnInit {

  pdfSrc = "../../assets/hello_.pdf";
  testSrc: any;
  base64: any;
  base64Image: any;
  _currentFormValues: never[] | undefined;
  @ViewChild("myCanvas", { static: false }) canvas: ElementRef;


  constructor(
    private prepSignService: PrepSignService,
  ) {
    pdfjsLib.GlobalWorkerOptions.workerSrc = `../../../assets/pdfjs/build/pdf.worker.js`;
  }

  ngOnInit(): void {
    console.log("ngOnInit");
    this.prepSignService.loadTestPdf().subscribe((data) => {
      this.testSrc = data;
      this.blobToBase64(data).then((data) => {
        this.base64 = data;
        let pdfData = atob(this.base64.split(',')[1]);
        //
        var loadingTask = pdfjsLib.getDocument({ data: pdfData });
        loadingTask.promise.then(pdf => {
          //
          // Fetch the first page
          //
          pdf.getPage(1).then(page => {
            var scale = 1.5;
            var viewport = page.getViewport({ scale: scale });

            //
            // Prepare canvas using PDF page dimensions
            //
            var context = this.canvas.nativeElement.getContext("2d");
            this.canvas.nativeElement.height = viewport.height;
            this.canvas.nativeElement.width = viewport.width;

            //
            // Render PDF page into canvas context
            //
            var renderContext = {
              canvasContext: context,
              viewport: viewport
            };
            page.render(renderContext);
          });
        });



      });

    });


  }

  public loadCompleted(pdf: PDFDocumentProxy): void {
    this._currentFormValues = [];
    console.log(pdf._pdfInfo);
    for (let i = 1; i <= pdf.numPages; i++) {
      // track the current page
      pdf.getPage(i).then(p => {
        return p.getAnnotations();
      }).then(ann => {
        const annotations = (<any>ann) as any[];
        annotations
          .filter(a => a.subtype === 'Widget')
          .forEach(a => {
            console.log(a);
            // this._createInput(a);
          });
      });
    }
  }

  addSignFormRequest(): void {
    //convert testSrc image to base64
    let payload: PrepSignModel = {
      id: 1,
      base64: this.base64,
      name: "name",
      reason: "reason",
      location: "location",
      pageNumber: 1
    }
    this.prepSignService.presignRequest(payload).subscribe((data) => {
      console.log(data);
      this.blobToBase64(data).then((data) => {
        this.base64Image = data;
        this.testSrc = data;
        //convert base64 to PDFDocumentProxy
        this.loadCompleted(this.base64Image);
      }
      );
    }
    );
  }

  blobToBase64(blob: Blob) {
    return new Promise((resolve, _) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(
        reader.result as string
      );
      reader.readAsDataURL(blob);
    });
  }

  onFileSelected(data: any) {
    console.log(data);
    if (!data.target.files) {
      return;
    }
    const file = data.target.files[0];
    const reader = new FileReader();
    reader.onloadend = () => {
      this.base64 = reader.result as string;
      console.log(this.base64);

    };
    reader.readAsDataURL(file);
  }


}
