import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddFormComponent } from './add-form/add-form.component';
import { RouterModule, Routes } from '@angular/router';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { HttpClientModule } from '@angular/common/http';
import { PdsjsTestComponent } from './pdsjs-test/pdsjs-test.component';

const routes: Routes = [
  {path:"",component: AddFormComponent},
  {path:"pdfjs",component: PdsjsTestComponent}
  ]



@NgModule({
  declarations: [
    AddFormComponent,
    PdsjsTestComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    PdfViewerModule,
    NgxExtendedPdfViewerModule,
    HttpClientModule,
    PdfViewerModule

  ]
})
export class PrepSignModule { }
