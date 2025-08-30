import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Medida } from '../interfaces/medida';
import { TipoAnimal } from '../interfaces/tipo-animal';

@Injectable({
  providedIn: 'root'
})
export class MedidaService {
  private apiUrl = `${environment.api}/medida`;
  private apiTipoAnimalUrl = `${environment.api}/tipoanimal`;

  constructor() {}

  private getAuthHeaders(): { [key: string]: string } {
    const token = localStorage.getItem('token') || '';
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
  }

  private getAuthHeadersOutType(): { [key: string]: string } {
    const token = localStorage.getItem('token') || '';
    return {
      'Authorization': `Bearer ${token}`
    };
  }

  async crearMedida(medida: Medida): Promise<string> {
    const response = await fetch(this.apiUrl, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(medida)
    });

    if (!response.ok) throw new Error('Error al crear medida');

    return await response.text();
  }

  async subirImagenMedida(imagen: File): Promise<string> {
    const formData = new FormData();
    formData.append('imagen', imagen);

    const response = await fetch(`${this.apiUrl}/imagen`, {
      method: 'POST',
      headers: this.getAuthHeadersOutType(), // no se debe incluir Content-Type
      body: formData
    });

    if (!response.ok) throw new Error('Error al subir la imagen');

    return await response.text();
  }

  async getTipos(): Promise<TipoAnimal[]> {
    const response = await fetch(this.apiTipoAnimalUrl, {
      headers: this.getAuthHeaders()
    });

    if (!response.ok) throw new Error('Error al obtener los tipos');

    return await response.json();
  }

  async getMedidas(): Promise<Medida[]> {
    const response = await fetch(this.apiUrl, {
      headers: this.getAuthHeaders()
    });

    if (!response.ok) throw new Error('Error al obtener las medidas');

    const medidasRaw = await response.json();

    if (!Array.isArray(medidasRaw)) throw new Error('Respuesta inválida del servidor');

    return medidasRaw.map((m: any) => ({
      idMedida: m.idMedida,
      nombreComun: m.nombreComun,
      nombreCientifico: m.nombreCientifico,
      tallaMinima: m.tallaMinima,
      imagen: m.imagen,
      idTipo: m.idTipo
    }));
  }

  async getImagenBlob(nombre: string): Promise<string> {
    const response = await fetch(`${this.apiUrl}/imagen/${encodeURIComponent(nombre)}`, {
      headers: this.getAuthHeadersOutType()
    });

    if (!response.ok) throw new Error(`No se pudo obtener la imagen: ${response.statusText}`);

    const blob = await response.blob();
    return URL.createObjectURL(blob);
  }

  async EditarMedida(medida: Medida): Promise<string> {
    const response = await fetch(`${this.apiUrl}/${medida.idMedida}`, {
      method: 'PUT',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(medida)
    });

    if (!response.ok) throw new Error('Error al editar medida');

    return await response.text();
  }

  async EliminarMedida(medida: Medida): Promise<string> {
    const response = await fetch(`${this.apiUrl}/${medida.idMedida}`, {
      method: 'DELETE',
      headers: this.getAuthHeaders()
    });

    if (!response.ok) throw new Error('Error al eliminar medida');

    return await response.text();
  }
}
