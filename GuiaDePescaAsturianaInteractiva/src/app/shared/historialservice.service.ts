import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Historial } from './interfaces/historial';
import { DomSanitizer } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class HistorialserviceService {

  constructor(private sanitizer: DomSanitizer) {}

  private getAuthHeaders(): Headers {
    const token = localStorage.getItem('token') || '';
    return new Headers({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  private getAuthHeadersNoType(): Headers {
    const token = localStorage.getItem('token') || '';
    return new Headers({
      'Authorization': `Bearer ${token}`
    });
  }

  async getHistorial(idPunto: number): Promise<Historial[]> {
    const response = await fetch(`${environment.api}/historial/punto/${idPunto}`, {
      method: 'GET',
      headers: this.getAuthHeaders()
    });

    if (!response.ok) throw new Error('Error al obtener historial');

    return await response.json();
  }

  async obtenerImagen(nombre: string): Promise<string> {
    const response = await fetch(`${environment.api}/imagen/archivo/${nombre}`, {
      headers: this.getAuthHeadersNoType()
    });

    if (!response.ok) throw new Error('No se pudo obtener la imagen');

    const blob = await response.blob();
    return URL.createObjectURL(blob);
  }

  async addHistorial(historial: Historial): Promise<Historial> {
    const response = await fetch(`${environment.api}/historial`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(historial)
    });

    if (!response.ok) throw new Error('Error al guardar captura');

    return await response.json();
  }

  async subirImagen(file: File, idHistorial: number): Promise<void> {
    const formData = new FormData();
    formData.append('imagen', file);
    formData.append('idHistorial', idHistorial.toString());

    const response = await fetch(`${environment.api}/imagen/subir`, {
      method: 'POST',
      headers: this.getAuthHeadersNoType(),
      body: formData
    });

    if (!response.ok) throw new Error('Error al subir imagen');
  }

  async actualizarHistorial(historial: Historial): Promise<Historial> {
    const response = await fetch(`${environment.api}/historial/${historial.idHistorial}`, {
      method: 'PUT',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(historial)
    });

    if (!response.ok) throw new Error('Error al actualizar captura');

    return await response.json();
  }

  async borrarHisorial(idHistorial: number): Promise<void> {
    const response = await fetch(`${environment.api}/historial/${idHistorial}`, {
      method: 'DELETE',
      headers: this.getAuthHeaders()
    });

    if (!response.ok) throw new Error('Error al eliminar captura');
  }

  async borrarImagen(idImagen: number): Promise<void> {
    const response = await fetch(`${environment.api}/imagen/${idImagen}`, {
      method: 'DELETE',
      headers: this.getAuthHeaders()
    });

    if (!response.ok) throw new Error('Error al eliminar imagen');
  }

  async getImagenesDeHistorial(idHistorial: number): Promise<string[]> {
    const response = await fetch(`${environment.api}/imagen/historial/${idHistorial}`, {
      method: 'GET',
      headers: this.getAuthHeadersNoType()
    });

    if (!response.ok) throw new Error('Error al obtener imágenes');

    return await response.json();
  }
}
