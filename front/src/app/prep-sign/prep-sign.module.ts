import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddFormComponent } from './add-form/add-form.component';
import { RouterModule, Routes } from '@angular/router';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { HttpClientModule } from '@angular/common/http';

const routes: Routes = [
  {path:"",component: AddFormComponent},
  ]



@NgModule({
  declarations: [
    AddFormComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    PdfViewerModule,
    NgxExtendedPdfViewerModule,
    HttpClientModule
  ]
})
export class PrepSignModule { }
