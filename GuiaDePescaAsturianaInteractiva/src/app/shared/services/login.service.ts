import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UsuarioLogin } from '../interfaces/usuario-login';
import { environment } from 'src/environments/environment';
import { CapacitorHttp } from '@capacitor/core';


@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor() { 

  }
  private httpClient:HttpClient=inject(HttpClient);

login(usuario: UsuarioLogin): Promise<any> {
  const options = {
    url: `${environment.api}/auth/login`,
    headers: {
      'Content-Type': 'application/json'
    },
    data: usuario
  };

  
  return CapacitorHttp.post(options);
}

  recuperarPassword(correo: string): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.httpClient.post<string>(
      `${environment.api}/auth/recuperarPassword`,
      JSON.stringify(correo),
      {
        headers,
        responseType: 'text' as 'json'  
      }
  );
}



}






