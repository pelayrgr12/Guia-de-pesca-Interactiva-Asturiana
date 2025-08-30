import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Usuario } from '../interfaces/Usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {

  constructor() {}

  private getAuthHeaders(): Headers {
    const token = localStorage.getItem('token') || '';
    return new Headers({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  async getUsuario(): Promise<Usuario> {
    const response = await fetch(`${environment.api}/usuario/me`, {
      method: 'GET',
      headers: this.getAuthHeaders()
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    return await response.json();
  }

  async getUsuarios(): Promise<Usuario[]> {
    const response = await fetch(`${environment.api}/usuario`, {
      method: 'GET',
      headers: this.getAuthHeaders()
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    return await response.json();
  }

  async actualizarHabilitado(id: number, habilitado: boolean): Promise<void> {
    const data = { id, habilitado };

    const response = await fetch(`${environment.api}/usuario/habilitar`, {
      method: 'PATCH',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    // No se retorna nada si todo va bien
  }
}
