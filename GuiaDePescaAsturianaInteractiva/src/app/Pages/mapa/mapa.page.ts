import { Component, OnInit, OnDestroy, inject, ViewEncapsulation } from '@angular/core';
import * as L from 'leaflet';
import { PuntoService } from 'src/app/shared/services/punto.service';
import { PuntoMapa } from 'src/app/shared/interfaces/punto-mapa';
import {
  ActionSheetController,
  AlertController,
  IonBackButton,
  IonButton,
  IonButtons,
  IonContent,
  IonFooter,
  IonHeader,
  IonTitle,
  IonToolbar,
  ToastController
} from "@ionic/angular/standalone";
import { ActivatedRoute, Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { addIcons } from 'ionicons';
import { trash, close, eye } from 'ionicons/icons';
import { firstValueFrom } from 'rxjs';

// Icono personalizado para los marcadores del mapa
const customIcon = L.icon({
  iconUrl: 'assets/img/marker-icon.png',
  iconRetinaUrl: 'assets/img/marker-icon-2x.png',
  shadowUrl: 'assets/img/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

@Component({
  selector: 'app-mapa',
  templateUrl: './mapa.page.html',
  styleUrls: ['./mapa.page.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: true,
  imports: [
    IonHeader, IonToolbar, IonTitle, IonContent,
    IonButtons, IonButton
  ]
})
export class MapaPage implements OnInit, OnDestroy {

  private router: Router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private puntoServicio = inject(PuntoService);
  private toastController = inject(ToastController);
  private actionSheet = inject(ActionSheetController);
  private alertController = inject(AlertController);

  private map?: L.Map;
  idSeleccionado: number | undefined;

  constructor() {
    addIcons({
      'trash': trash,
      'close': close,
      'eye': eye,
    });
  }

  /** Inicialización de la página */
  ngOnInit() {}

  /** Refresca la vista centrando el mapa y cargando los puntos */
  Refresh() {
    if (this.map) {
      this.map.setView([43.36, -5.85], 9);
    }
    this.loadPuntos();
  }

  /**
   * Carga los puntos del usuario y los coloca en el mapa.
   * @param idSeleccionado ID del punto a centrar opcionalmente
   */
  async loadPuntos(idSeleccionado?: number) {
    if (!this.map) return;

    this.map.eachLayer((layer) => {
      if (layer instanceof L.Marker) {
        this.map?.removeLayer(layer);
      }
    });

    try {
      const puntos = await this.puntoServicio.getListaPuntos();
      let puntoSeleccionado: PuntoMapa | undefined;

      puntos.forEach(punto => {
        const marker = L.marker([punto.latitud, punto.longitud], { icon: customIcon })
          .addTo(this.map!)
          .bindPopup(punto.nombre || 'Punto guardado');

        marker.on('click', () => {
          this.map?.setView([punto.latitud, punto.longitud], 10);
          this.confirmarEliminarPunto(punto);
        });

        if (punto.idPunto === idSeleccionado) {
          puntoSeleccionado = punto;
        }
      });

      if (puntoSeleccionado) {
        this.map?.setView([puntoSeleccionado.latitud, puntoSeleccionado.longitud], 11);
      }

    } catch (error) {
      console.error('Error al cargar puntos del usuario:', error);
    }
  }

  /**
   * Evento de Ionic cuando la vista entra. Inicializa el mapa y puntos.
   */
  async ionViewDidEnter() {
    const params = await firstValueFrom(this.route.queryParams);
    const idSeleccionado = +params['puntoId'];

    this.initMap();

    setTimeout(() => this.map?.invalidateSize(), 500);

    await this.loadPuntos(!isNaN(idSeleccionado) ? idSeleccionado : undefined);

    if (!isNaN(idSeleccionado)) {
      this.router.navigate([], {
        queryParams: {},
        replaceUrl: true,
        queryParamsHandling: ''
      });
    }
  }

  /** Elimina el mapa al salir del componente */
  ngOnDestroy() {
    if (this.map) {
      this.map.remove();
      this.map = undefined;
    }
  }

  /** Inicializa el mapa con centro en Asturias */
  initMap() {
    if (this.map) return;

    this.map = L.map('map').setView([43.36, -5.85], 9);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const punto: PuntoMapa = {
        nombre: e.latlng.toString(),
        latitud: e.latlng.lat,
        longitud: e.latlng.lng
      };
      this.confirmarGuardarPunto(punto);
    });

    this.idSeleccionado = undefined;
  }

  /**
   * Guarda un nuevo punto en el servidor y lo muestra en el mapa
   * @param punto Punto geográfico a guardar
   */
  async guardarPunto(punto: PuntoMapa) {
    try {
      await this.puntoServicio.guardarPuntos(punto);

      L.marker([punto.latitud, punto.longitud], { icon: customIcon })
        .addTo(this.map!)
        .bindPopup(punto.nombre || 'Punto guardado')
        .openPopup();

      await this.mostrarMensaje('Punto guardado correctamente', 'success');
    } catch (err: any) {
      console.error('Error al guardar punto:', err);

      let mensaje = 'Error al guardar el punto';
      if (err?.status === 409) {
        mensaje = 'Ya existe un punto con ese nombre';
      }

      await this.mostrarMensaje(mensaje, 'danger');
    }
  }

  /**
   * Muestra un mensaje tipo toast en la parte superior
   * @param mensaje Texto a mostrar
   * @param color Tipo de mensaje ('success' | 'danger')
   */
  async mostrarMensaje(mensaje: string, color: 'success' | 'danger') {
    const toast = await this.toastController.create({
      message: mensaje,
      duration: 3000,
      position: 'top',
      color: color
    });
    toast.present();
  }

  /**
   * Abre un alert para introducir nombre y descripción antes de guardar el punto
   * @param punto Punto a confirmar
   */
  async confirmarGuardarPunto(punto: PuntoMapa) {
    const alert = await this.alertController.create({
      header: 'Nuevo Punto',
      subHeader: `Lat: ${punto.latitud.toFixed(5)} | Lng: ${punto.longitud.toFixed(5)}`,
      inputs: [
        {
          name: 'nombre',
          type: 'text',
          placeholder: 'Nombre del punto'
        },
        {
          name: 'descripcion',
          type: 'text',
          placeholder: 'Descripción del punto',
          attributes: {
            rows: 3
          }
        }
      ],
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Guardar',
          handler: async (data) => {
            punto.nombre = data.nombre;
            punto.descripcion = data.descripcion;
            await this.guardarPunto(punto);
          }
        }
      ]
    });

    await alert.present();
  }

  /**
   * Borra un punto específico del servidor y del mapa
   * @param punto Punto a eliminar
   */
  async borrarPunto(punto: PuntoMapa) {
    try {
      await this.puntoServicio.eliminarPunto(punto);
      this.map?.eachLayer((layer) => {
        if (layer instanceof L.Marker) {
          const markerLatLng = layer.getLatLng();
          if (
            markerLatLng.lat.toFixed(5) === punto.latitud.toFixed(5) &&
            markerLatLng.lng.toFixed(5) === punto.longitud.toFixed(5)
          ) {
            this.map?.removeLayer(layer);
          }
        }
      });
      const toast = await this.toastController.create({
        message: 'Punto eliminado correctamente',
        duration: 2000,
        color: 'success'
      });
      await toast.present();
    } catch (error) {
      console.error('Error al eliminar punto:', error);
      const toast = await this.toastController.create({
        message: 'Error al eliminar el punto',
        duration: 2000,
        color: 'danger'
      });
      await toast.present();
    }
  }

  /**
   * Muestra un menú contextual con opciones para el punto: ver historial o eliminar
   * @param punto Punto sobre el que se lanza el menú
   */
  async confirmarEliminarPunto(punto: PuntoMapa) {
    const mensaje = await this.actionSheet.create({
      header: 'Información del Punto',
      subHeader: `Nombre: ${punto.nombre} | Lat: ${punto.latitud.toFixed(5)} | Lng: ${punto.longitud.toFixed(5)}
      | Descripción: ${punto.descripcion || 'No disponible'}`,
      buttons: [
        {
          text: 'Ver Historial',
          icon: 'eye',
          handler: () => {
            this.router.navigate(['/tabs/historial', punto.idPunto]);
          }
        },
        {
          text: 'Eliminar',
          icon: 'trash',
          handler: () => {
            this.borrarPunto(punto);
          }
        },
        {
          text: 'Cancelar',
          icon: 'close',
          role: 'cancel'
        }
      ]
    });

    await mensaje.present();
  }


}
