import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { PuntoMapa } from '../interfaces/punto-mapa';

@Injectable({
  providedIn: 'root'
})
export class PuntoService {

  constructor() {}

  private getAuthHeaders(): Headers {
    const token = localStorage.getItem('token') || '';
    return new Headers({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  async guardarPuntos(punto: PuntoMapa): Promise<PuntoMapa> {
    const response = await fetch(`${environment.api}/punto`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(punto)
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    return await response.json();
  }

  async modificarPuntosCapacitor(idPunto: number, nombrePunto: string): Promise<any> {
    const response = await fetch(`${environment.api}/punto/modificar/${idPunto}`, {
      method: 'PATCH',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(nombrePunto)
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    return await response.json();
  }

  async modificarPunto(punto: PuntoMapa): Promise<string> {
    const response = await fetch(`${environment.api}/punto/modificar`, {
      method: 'PUT',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(punto)
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    return await response.text();
  }

  async eliminarPunto(punto: PuntoMapa): Promise<string> {
    const response = await fetch(`${environment.api}/punto`, {
      method: 'DELETE',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(punto)
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    return await response.text();
  }

  async getListaPuntos(): Promise<PuntoMapa[]> {
    const response = await fetch(`${environment.api}/punto/mis-puntos`, {
      method: 'GET',
      headers: this.getAuthHeaders()
    });

    if (!response.ok) {
      const mensaje = await response.text();
      throw { status: response.status, mensaje };
    }

    return await response.json();
  }
}
