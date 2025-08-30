import { Component, inject, OnInit, ViewEncapsulation, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonButton, IonButtons, IonCard, IonCardContent, IonCardHeader,
  IonCardTitle, IonContent, IonFooter, IonHeader, IonList,
  IonTitle, IonToolbar, IonInput, IonTextarea,
  IonLabel,
  IonItem,
  ToastController,
  IonDatetime,
  IonIcon,
  IonDatetimeButton,
  IonModal,
  IonPopover,
  IonNote,
  IonRow, IonImg } from '@ionic/angular/standalone';
import { ActivatedRoute, Router } from '@angular/router';
import { formatDate } from '@angular/common';
import { Historial, ImagenHistorial } from 'src/app/shared/interfaces/historial';
import { jwtDecode } from 'jwt-decode';
import { HistorialserviceService } from 'src/app/shared/historialservice.service';
import { addIcons } from 'ionicons';
import { calendarOutline, closeCircle, imagesOutline, searchOutline } from 'ionicons/icons';
//detector par el doom
import { ChangeDetectorRef } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { AlertController } from '@ionic/angular';

/**
 * Página para visualizar, filtrar y gestionar el historial de capturas
 */
@Component({
  selector: 'app-historial',
  templateUrl: './historial.page.html',
  styleUrls: ['./historial.page.scss'],
  standalone: true,
  imports: [IonImg, IonContent, IonHeader, IonTitle,
    IonToolbar, CommonModule, FormsModule,IonButtons,
    IonButton,IonCard,IonCardContent,
    IonCardTitle,IonCardHeader,IonList,IonLabel,
    IonItem,IonInput,IonTextarea,IonDatetime,IonIcon
    ,IonDatetimeButton,IonModal,IonPopover,IonNote,IonRow ]
})
export class HistorialPage implements OnInit {

  private activateRotuer:ActivatedRoute = inject(ActivatedRoute);
  private historialService:HistorialserviceService = inject(HistorialserviceService);
  private alertController = inject(AlertController);
  private toastController = inject(ToastController);

  /** Historiales completos y filtrados */
  historiales: Historial[] = [];
  historialesFiltrados: Historial[] = [];

  /** Variables de control de estado */
  mostrarFormulario = false;
  modalAbierto: boolean = false;

  /** Identificador del punto seleccionado */
  idPunto!: number;

  /** Objeto de nueva captura a registrar */
  nuevaCaptura!: Historial;

  /** Imágenes relacionadas con el historial */
  imagenesHistorial: string[] = [];
  imagenesBlob: { [nombre: string]: SafeUrl } = {};

  /** Filtros de búsqueda por texto y fechas */
  filtroTexto: string = '';
  mostrarFechas: boolean = false;
  fechaDesde: string | null = null;
  fechaHasta: string | null = null;

  /** Imagenes seleccionadas y sus previsualizaciones */
  archivosImagenes: File[] = [];
  selectedFileNames: string[] = [];
  imagePreviewUrls: string[] = [];
  @ViewChild("fileInput") fileInput!: ElementRef;

  /** Fecha temporal y estado de campo activo para selección de fechas */
  fechaTemporal: string = '';
  campoActivo: 'desde' | 'hasta' | null = null;
  fechaActual: string = new Date().toISOString();

  /** Copia de los historiales antes de editar para revertir cambios */
  copiasOriginales: { [idHistorial: number]: Historial } = {};

  constructor(private router: Router,private cdr: ChangeDetectorRef) {
          addIcons({
                      'search-outline': searchOutline,
                      'calendar-outline': calendarOutline,
                      'images-outline': imagesOutline,
                      'close-circle':closeCircle
                    });
                  }
  

  ngOnInit() {
    this.fechaActual = formatDate(new Date(), 'yyyy-MM-ddTHH:mm', 'en-US');
      const navigation = this.router.getCurrentNavigation();
      const id = this.activateRotuer.snapshot.paramMap.get('idPunto') ?? '';
      this.idPunto = Number(id);
     this.getHistorial(this.idPunto!);
  }

   /** Muestra imágenes asociadas a un historial */
  getImagenes(idHistorial: number) {
    this.historialService.getImagenesDeHistorial(idHistorial).then(imagenes => {
    this.imagenesHistorial = imagenes;
  });

  }

   /** Obtiene historiales de un punto y carga sus imágenes */
  async getHistorial(idPunto: number) {
  if (!idPunto) {
    return;
  }

  try {
    const data = await this.historialService.getHistorial(idPunto);
    this.historiales = data;
    console.log('Historiales:', this.historiales);
    await this.cargarImagenesDeHistoriales(this.historiales);
    this.aplicarFiltros();
  } catch (error) {
    console.error('Error al obtener historial:', error);
  }
}
/** Carga las imágenes de los historiales usando el servicio */
 async cargarImagenesDeHistoriales(historiales: Historial[]) {
    for (const historial of historiales) {
      for (const img of historial.imagenes || []) {
              const nombre = img.nombre;
        if (!this.imagenesBlob[nombre]) {
          try {
             const url = await this.historialService.obtenerImagen(nombre);
              this.imagenesBlob[nombre] = url;
       
          } catch (error) {
            console.error('Error cargando imagen:', nombre, error);
          }
        }
      }
    }
  }


  /** Guarda una nueva captura con imágenes */
async guardarCaptura() {
  if (!this.nuevaCaptura.descripcion || this.nuevaCaptura.descripcion.trim() === '') {
    this.mostrarMensaje('La descripción no puede estar vacía', 'danger');
    return;
  }

  if (!this.nuevaCaptura.imagenes || this.nuevaCaptura.imagenes.length === 0) {
    this.mostrarMensaje('Debes agregar al menos una imagen', 'danger');
    return;
  }

  try {
    this.nuevaCaptura.idPunto = this.idPunto;

    const historialGuardado = await this.historialService.addHistorial(this.nuevaCaptura);

    for (let archivo of this.archivosImagenes) {
      await this.historialService.subirImagen(archivo, historialGuardado.idHistorial!);
    }

    this.mostrarFormulario = false;
    this.nuevaCaptura = {
      idPunto: this.idPunto,
      fecha: formatDate(new Date(), 'yyyy-MM-ddTHH:mm', 'en-US'),
      descripcion: '',
      imagenes: []
    };
    this.archivosImagenes = [];
    this.selectedFileNames = [];
    this.imagePreviewUrls = [];

    this.mostrarMensaje('Captura guardada correctamente', 'success');
    this.getHistorial(this.idPunto);
  } catch (err) {
    console.error('Error al guardar captura', err);
    this.mostrarMensaje('Error al guardar la captura', 'danger');
  }
}

 /** Carga imágenes seleccionadas para una nueva captura */
cargarImagenes(event: any) {
  const files: FileList = event.target.files;

  if (files && files.length) {
    this.selectedFileNames = this.selectedFileNames || [];
    this.imagePreviewUrls = this.imagePreviewUrls || [];
    this.archivosImagenes = this.archivosImagenes || [];
    this.nuevaCaptura.imagenes = this.nuevaCaptura.imagenes || [];

    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      if (!this.selectedFileNames.includes(file.name)) {
        this.archivosImagenes.push(file);
        this.selectedFileNames.push(file.name);

        this.nuevaCaptura.imagenes.push({ nombre: file.name });

        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.imagePreviewUrls.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
    }

    event.target.value = '';
  }
}

  /** Elimina una imagen aún no subida */
 removeImage(index: number, event: Event) {
  /*
  const seguro = await this.confirmarAccion('¿Estás seguro de que deseas eliminar esta imagen?');
  
  if (!seguro) {
    return;
  }
    */
  event.stopPropagation();

  const nombre = this.archivosImagenes[index]?.name;

  this.archivosImagenes.splice(index, 1);
  this.selectedFileNames.splice(index, 1);
  this.imagePreviewUrls.splice(index, 1);

  const idx = this.nuevaCaptura.imagenes?.findIndex(img => img.nombre === nombre);
  if (idx !== -1 && idx !== undefined) {
    this.nuevaCaptura.imagenes?.splice(idx, 1);
  }
}

onFechaChange(event: any, campo: 'desde' | 'hasta') {
  const valor = event.target.value;
  if (campo === 'desde') {
    this.fechaDesde = valor;
  } else {
    this.fechaHasta = valor;
  }
}
/** Confirma la fecha seleccionada */
confirmarFecha(event: any) {
  const valor: string = event.detail.value; 
  const soloFecha = valor.split('T')[0]; 

  if (this.campoActivo === 'desde') {
    this.fechaDesde = soloFecha;
  } else if (this.campoActivo === 'hasta') {
    this.fechaHasta = soloFecha;
  }

  this.modalAbierto = false;
  this.campoActivo = null;
}

cancelarFiltro() {
  this.fechaDesde = null;
  this.fechaHasta = null;
  this.mostrarFechas = false;
  this.aplicarFiltros();
}
/** Aplica filtros de texto y fecha sobre los historiales */
aplicarFiltros() {
  this.historialesFiltrados = this.historiales.filter(h => {
    const descripcionOk = !this.filtroTexto || (h.descripcion ?? '').toLowerCase().includes(this.filtroTexto.toLowerCase());

    const fechaRegistro = new Date(h.fecha ?? '');
    const desde = this.fechaDesde ? new Date(this.fechaDesde) : null;
    const hasta = this.fechaHasta ? new Date(this.fechaHasta) : null;

    if (hasta) {
      hasta.setHours(23, 59, 59, 999);
    }

    const fechaOk =
      (!desde || fechaRegistro >= desde) &&
      (!hasta || fechaRegistro <= hasta);

    return descripcionOk && fechaOk;
  });
}
 /** Elimina un historial tras confirmar */
async eliminarHistorial(id:number){

  const seguro = await this.confirmarAccion('¿Estás seguro de que deseas eliminar la captura?');

    if (!seguro) {
    return;
  }


  if (!id) {
    this.mostrarMensaje('Error al intentar la captura', 'danger');
    return;
  }
  this.historialService.borrarHisorial(id).then(()=>{
    this.historialesFiltrados = this.historialesFiltrados.filter(h => h.idHistorial !== id);
    this.historiales = this.historiales.filter(h => h.idHistorial !== id);
    this.mostrarMensaje('Captura eliminada correctamente', 'success');
  })

}
/** Lanza un alert de confirmación */
async confirmarAccion(mensaje: string): Promise<boolean> {
  return new Promise(async (resolve) => {
    const alert = await this.alertController.create({
      header: 'Confirmación',
      message: mensaje,
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel',
          handler: () => resolve(false),
        },
        {
          text: 'Aceptar',
          handler: () => resolve(true),
        },
      ],
    });

    await alert.present();
  });
}


/** Activa el modo edición en un historial */
editarHistorial(index: number) {
  const historial = this.historialesFiltrados[index];
  historial.editando = true;

  this.copiasOriginales[historial.idHistorial!] = JSON.parse(JSON.stringify(historial));

  if (historial.fecha) {
    const fecha = new Date(historial.fecha);
    historial.fecha = fecha.toISOString().split('T')[0];
  }
}



async mostrarMensaje(mensaje: string, color: 'success' | 'danger') {
    const toast = await this.toastController.create({
      message: mensaje,
      duration: 3000,
      position: 'top',
      color: color
    });
    toast.present();
  }

  toggleFormulario() {
    this.mostrarFormulario = !this.mostrarFormulario;
    if (this.mostrarFormulario) {
      this.nuevaCaptura = {
        fecha: new Date().toISOString(),
        descripcion: '',
        imagenes: []
      };
    }
  }


  cancelarFormulario() {
  this.mostrarFormulario = false;

  this.nuevaCaptura = {
    fecha: new Date().toISOString(), 
    descripcion: '',
    imagenes: []
  };
  this.imagePreviewUrls = [];
  this.selectedFileNames = [];
  this.archivosImagenes = [];

  if (this.fileInput) {
    this.fileInput.nativeElement.value = '';
  }
}



//----------------------------------------------------------------edicion 



async eliminarImagenExistente(historial: Historial, index: number) {
  const imagen = historial.imagenes![index];
  imagen.pendienteEliminar = true;
}

  /** Carga nuevas imágenes para un historial en edición */
cargarImagenesEdicion(event: any, historial: Historial) {
  const files: FileList = event.target.files;

  historial.nuevasImagenes = historial.nuevasImagenes || [];
  historial.nuevasPreview = historial.nuevasPreview || [];

  for (let i = 0; i < files.length; i++) {
    const file = files[i];

    if (!historial.nuevasImagenes.find((f: File) => f.name === file.name)) {
      historial.nuevasImagenes.push(file);

      const reader = new FileReader();
      reader.onload = (e: any) => {
        historial.nuevasPreview!.push(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  event.target.value = '';
}
/** Elimina una imagen recién añadida en modo edición */
eliminarNuevaImagen(historial: Historial, index: number) {
  historial.nuevasImagenes?.splice(index, 1);
  historial.nuevasPreview?.splice(index, 1);
}

/** Guarda los cambios hechos a un historial en edición */
async guardarEdicion(index: number) {
  const historial = this.historiales[index];

  try {
    for (let img of historial.imagenes || []) {
      if (img.pendienteEliminar && img.id) {
        await this.historialService.borrarImagen(img.id);
      }
    }

    historial.imagenes = historial.imagenes?.filter(img => !img.pendienteEliminar);

    const actualizado = await this.historialService.actualizarHistorial(historial);

    for (let archivo of historial.nuevasImagenes || []) {
      await this.historialService.subirImagen(archivo, actualizado.idHistorial!);
    }

    historial.editando = false;
    historial.nuevasImagenes = [];
    historial.nuevasPreview = [];

    this.mostrarMensaje('Captura actualizada correctamente', 'success');
    this.getHistorial(this.idPunto); 
  } catch (error) {
    console.error('Error al actualizar captura', error);
    this.mostrarMensaje('Error al actualizar la captura', 'danger');
  }
}

/** Cancela la edición y restaura el historial original */
cancelarEdicion(index: number) {
  const h = this.historialesFiltrados[index];
  const copia = this.copiasOriginales[h.idHistorial!];

  if (copia) {
    this.historialesFiltrados[index] = JSON.parse(JSON.stringify(copia));
    const idxOriginal = this.historiales.findIndex(hist => hist.idHistorial === h.idHistorial);
    if (idxOriginal !== -1) {
      this.historiales[idxOriginal] = JSON.parse(JSON.stringify(copia));
    }

    delete this.copiasOriginales[h.idHistorial!];
  }

  this.historialesFiltrados[index].editando = false;
}


/** Verifica que el usuario es ADMIN, si no lo redirige */
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
      }else if (rol === 'ADMIN') {
        this.router.navigate(['/menu-administrador']);
      }
    } catch (error) {
      this.router.navigate(['/login']);
    }
  }

}
