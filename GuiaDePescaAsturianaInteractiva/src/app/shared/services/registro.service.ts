import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { UsuarioRegistro } from '../interfaces/usuario-registro';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RegistroService {

  private httpClient:HttpClient=inject(HttpClient);
  constructor() { }

  registro(usuarioRegistro:UsuarioRegistro):Observable<any>{
    let headers=new HttpHeaders({
      "Content-Type":"application/json"
    });
    return this.httpClient.post(`${environment.api}/auth/registro`,
      JSON.stringify(usuarioRegistro),{headers});

  }
}
