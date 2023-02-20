
import { Injectable } from "@angular/core";
import { HttpClient} from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({providedIn: 'root'})
export class PrepSignService {
  constructor (private http: HttpClient) {}

  loadPresignPdf(): Observable<any> {
    return this.http.get(environment.apiUrl + '/presign');
  }

  //load hello2.pdf from assets folder
  loadTestPdf(): Observable<any> {
    return this.http.get('../../assets/Hello2.pdf', {responseType: 'blob'});
  }
}
