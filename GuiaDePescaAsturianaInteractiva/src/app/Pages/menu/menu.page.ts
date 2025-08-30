import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonCard,
  IonCardHeader, IonCardTitle, IonCardContent, IonFooter, IonButtons,
  IonButton, IonIcon, IonItem, IonLabel, IonInput,
  IonCol, IonRow, IonGrid, IonCardSubtitle, IonSpinner, IonText
} from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { OpenWeatherService } from 'src/app/shared/services/open-weather.service';
import { addIcons } from 'ionicons';
import {
  cloudOutline, navigateOutline, searchOutline,
  speedometerOutline, thunderstormOutline, waterOutline
} from 'ionicons/icons';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.page.html',
  styleUrls: ['./menu.page.scss'],
  standalone: true,
  imports: [
    IonLabel, IonItem, IonCardTitle, IonCardHeader, IonCard,
    IonContent, IonHeader, IonTitle, IonToolbar,
    CommonModule, FormsModule, IonCardContent,
    IonFooter, IonButtons, IonButton, IonIcon, IonInput, IonCol,
    IonRow, IonGrid, IonCardSubtitle, IonSpinner, IonText
  ]
})
export class MenuPage implements OnInit {

  private router: Router = inject(Router);
  private openWeather = inject(OpenWeatherService);

  /** Ciudad por defecto para la consulta del clima */
  ciudad: string = 'Gijon';

  /** Objeto con los datos del clima obtenidos */
  clima: any = null;

  /** Indica si se están cargando los datos del clima */
  cargando = false;

  /** Mensaje de error si ocurre al consultar el clima */
  error = "";

  constructor() {
    addIcons({
      'navigate-outline': navigateOutline,
      'cloud-outline': cloudOutline,
      'thunderstorm-outline': thunderstormOutline,
      'search-outline': searchOutline,
      'water-outline': waterOutline,
      'speedometer-outline': speedometerOutline,
    });
  }

  /**
   * Ciclo de vida de la vista: ejecuta la búsqueda del clima al iniciar
   */
  ngOnInit() {
    this.buscarClima();
  }

  /**
   * Devuelve una descripción textual de la dirección del viento a partir de grados
   * @param grados Valor en grados del viento (0-360)
   * @returns Dirección cardinal como cadena (Norte, Este, etc.)
   */
  getDescripcionViento(grados: number): string {
    const direcciones = ["Norte", "Noreste", "Este", "Sureste", "Sur", "Suroeste", "Oeste", "Noroeste"];
    const indice = Math.round((grados % 360) / 45) % 8;
    return direcciones[indice];
  }

  /**
   * Realiza la llamada al servicio OpenWeather para obtener el clima actual
   */
  buscarClima() {
    this.cargando = true;
    this.error = "";
    this.openWeather.getWeather(this.ciudad).subscribe({
      next: (data) => {
        console.log('Datos del clima:', data);
        this.clima = data;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al obtener el clima:', error);
        this.error = 'No se pudo obtener el clima.';
        this.cargando = false;
      }
    });
  }

  /**
   * Obtiene el path del icono del clima según el código proporcionado por OpenWeather
   * @param icon Código del icono proporcionado por la API de clima
   * @returns Ruta de imagen o recurso correspondiente al icono
   */
  getIcono(icon: string) {
    return this.openWeather.getIcono(icon);
  }

}
