import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  IonButton, IonButtons, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonContent,
  IonFooter, IonHeader, IonList, IonSearchbar, IonTitle, IonToolbar,
  ToastController, IonLabel, IonItem, IonTabButton, IonInput, IonIcon,
  AlertController
} from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { PuntoMapa } from 'src/app/shared/interfaces/punto-mapa';
import { PuntoService } from 'src/app/shared/services/punto.service';
import { FormsModule } from '@angular/forms';
import { jwtDecode } from 'jwt-decode';
import { addIcons } from 'ionicons';
import { arrowDown, camera, imagesOutline } from 'ionicons/icons';

@Component({
  selector: 'app-mis-puntos',
  templateUrl: './mis-puntos.page.html',
  styleUrls: ['./mis-puntos.page.scss'],
  standalone: true,
  imports: [
    IonIcon, IonInput, IonTabButton, IonItem, IonLabel,
    IonHeader, IonToolbar, IonTitle, IonContent,
    IonFooter, IonButtons, IonButton, IonList, IonCard,
    IonCardHeader, IonCardTitle, IonCardContent, CommonModule, FormsModule, IonSearchbar,
  ]
})
export class MisPuntosPage implements OnInit {

  private router: Router = inject(Router);
  private puntoServicio = inject(PuntoService);
  private toastController = inject(ToastController);
  private alertController = inject(AlertController);

  constructor() {
    addIcons({
      'images-outline': imagesOutline,
      'arrow-down': arrowDown,
      'camera': camera
    });
  }

  /** Lista completa de puntos */
  listaPuntos: PuntoMapa[] = [];

  /** Lista filtrada de puntos según búsqueda */
  listaPuntosFiltrados: PuntoMapa[] = [];

  /** Texto ingresado por el usuario para búsqueda */
  busqueda: string = '';

  /** ID del punto que se está editando (si aplica) */
  puntoEnEdicion: number | null = null;

  /** Nuevo nombre para el punto en edición */
  nuevoNombre: string = '';

  /** Nueva descripción para el punto en edición */
  nuevaDescripcion: string = '';

  /** Fecha actual usada como referencia */
  nuevaFecha: Date = new Date();

  /**
   * Inicializa la vista y carga los puntos
   */
  ngOnInit() {
    this.getPuntos();
  }

  /**
   * Muestra un toast informativo para guardar capturas
   */
  async presentToast() {
    const toast = await this.toastController.create({
      message: '¡Toca para guardar una captura!',
      icon: 'camera',
      position: 'top',
      duration: 2500,
      buttons: [
        {
          side: 'end',
          text: 'OK',
          role: 'cancel'
        }
      ],
      cssClass: 'custom-toast',
      animated: true
    });
    await toast.present();
  }

  /**
   * Recarga los puntos cada vez que se entra a la vista
   */
  async ionViewWillEnter() {
    await this.getPuntos();
  }

  /**
   * Navega al mapa con el punto seleccionado
   */
  verEnMapa(punto: PuntoMapa) {
    this.router.navigate(['tabs/mapa'], {
      queryParams: { puntoId: punto.idPunto }
    });
  }

  /**
   * Navega al historial del punto
   */
  verHistorial(idPunto: number) {
    this.router.navigate(['/tabs/historial', idPunto]);
  }

  /**
   * Activa el modo edición para un punto
   */
  editarMetodo(punto: PuntoMapa) {
    this.puntoEnEdicion = punto.idPunto!;
    this.nuevoNombre = punto.nombre;
    this.nuevaDescripcion = punto.descripcion || '';
  }

  /**
   * Cancela la edición del punto
   */
  cancelarEdicion() {
    this.puntoEnEdicion = null;
    this.nuevoNombre = '';
  }

  /**
   * Guarda los cambios realizados al punto editado
   */
  guardarCambios(punto: PuntoMapa) {
    const nombreLimpio = this.nuevoNombre.trim();

    if (!nombreLimpio) {
      this.mostrarMensaje('Nombre de punto no válido', 'danger');
      return;
    }

    punto.nombre = nombreLimpio;
    punto.descripcion = this.nuevaDescripcion;

    if (!punto || !punto.idPunto) {
      this.mostrarMensaje('Datos del punto inválidos', 'danger');
      return;
    }

    this.puntoServicio.modificarPunto(punto).then(() => {
      this.mostrarMensaje('Punto actualizado correctamente', 'success');
      this.getPuntos();
      this.cancelarEdicion();
    }).catch(() => {
      this.mostrarMensaje('Error al actualizar el punto', 'danger');
    });
  }

  /**
   * Muestra un mensaje tipo toast
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
   * Filtra los puntos por nombre usando el texto de búsqueda
   */
  filtrarPuntos() {
    const texto = this.busqueda.trim().toLowerCase();

    if (texto.length === 0) {
      this.listaPuntosFiltrados = this.listaPuntos;
      return;
    }

    this.listaPuntosFiltrados = this.listaPuntos.filter(punto =>
      punto.nombre?.toLowerCase().includes(texto)
    );
  }

  /**
   * Recarga la lista de puntos desde el servidor
   */
  async Refresh() {
    try {
      await this.getPuntos();

      const toast = await this.toastController.create({
        message: 'Puntos recargados correctamente',
        duration: 1250,
        color: 'success',
        position: 'bottom'
      });

      await toast.present();
    } catch (error) {
      console.error('Error recargando puntos:', error);

      const toast = await this.toastController.create({
        message: 'Error al recargar puntos',
        duration: 2000,
        color: 'danger',
        position: 'bottom'
      });

      await toast.present();
    }
  }

  /**
   * Obtiene todos los puntos del usuario
   */
  getPuntos() {
    this.puntoServicio.getListaPuntos().then((puntos: PuntoMapa[]) => {
      this.listaPuntos = puntos;
      this.listaPuntosFiltrados = puntos;
    }).catch((error) => {
      console.error('error ', error);
    });
  }

  /**
   * Elimina un punto tras confirmación del usuario
   */
  async eliminarPunto(punto: PuntoMapa) {
    const alert = await this.alertController.create({
      header: '⚠️ Confirmar eliminación',
      message: '¿Estás seguro de que deseas eliminar este punto?  Borraras todo el historial asociado a este punto.',
      cssClass: 'alerta-warning',
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Eliminar',
          role: 'destructive',
          handler: async () => {
            try {
              await this.puntoServicio.eliminarPunto(punto);
              await this.getPuntos();
              const toast = await this.toastController.create({
                message: 'Punto eliminado correctamente',
                duration: 1000,
                color: 'success',
                position: 'bottom'
              });
              await toast.present();
            } catch (error) {
              console.error('Error', error);
              const toast = await this.toastController.create({
                message: 'Error al eliminar el punto',
                duration: 1000,
                color: 'danger',
                position: 'bottom'
              });
              await toast.present();
            }
          }
        }
      ]
    });

    await alert.present();
  }

  /**
   * Verifica el rol del usuario y redirige al menú correspondiente
   */
  verificarAdmin() {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    try {
      const decodedToken: any = jwtDecode(token);
      const rol = decodedToken.rol;
      if (rol === 'USER') {
        this.router.navigate(['/menu']);
      } else if (rol === 'ADMIN') {
        this.router.navigate(['/menu-administrador']);
      }
    } catch (error) {
      console.error('Error al decodificar token:', error);
      this.router.navigate(['/login']);
    }
  }

  /**
   * Navega al menú correspondiente según el rol
   */
  navMenu() {
    this.verificarAdmin();
  }

  /** Navega al mapa */
  navMapa() {
    this.router.navigate(['/mapa']);
  }

  /** Navega al perfil */
  navPerfil() {
    this.router.navigate(['/perfil']);
  }

  /** Navega a la tabla de medidas */
  navTablaMedidas() {
    this.router.navigate(['/tabla-medidas']);
  }

}
