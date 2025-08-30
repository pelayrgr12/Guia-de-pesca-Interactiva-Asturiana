
import { Usuarioform } from '../interfaces/usuarioform';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PerfilService {

  constructor() { }

  async actualizarPerfil(usuario: Usuarioform): Promise<any> {
  const token = localStorage.getItem('token');

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  const response = await fetch(`${environment.api}/usuario/actualizar`,{
    method: 'PUT',
    headers: headers,
    body: JSON.stringify(usuario)
  }
  );

  if (response.status < 200 || response.status >= 300) {
    const error: any = new Error('Error HTTP');
    error.status = response.status;
    error.body = await response.text();
    throw error;
  }

  return await response.text();
}


}
