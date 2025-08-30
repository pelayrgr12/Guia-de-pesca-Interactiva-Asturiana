import { Component, inject, OnInit } from '@angular/core';
import {
  AlertController,
  IonButton, IonButtons, IonCard,
  IonCardContent, IonCardHeader, IonCardSubtitle,
  IonCardTitle, IonContent, IonFooter, IonHeader,
  IonIcon, IonImg, IonInput, IonItem, IonLabel,
  IonList, IonSearchbar, IonSelect,
  IonSelectOption, IonSpinner, IonTitle, IonToolbar,
  ToastController, IonText } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { TipoAnimal } from 'src/app/shared/interfaces/tipo-animal';
import { Medida } from 'src/app/shared/interfaces/medida';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MedidaService } from 'src/app/shared/services/medida.service';
import { environment } from 'src/environments/environment';
import { addIcons } from 'ionicons';
import { calendarOutline, closeCircle, imagesOutline, searchOutline } from 'ionicons/icons';
import { Icon } from 'ionicons/dist/types/components/icon/icon';

/**
 * Página encargada de mostrar, filtrar y gestionar las medidas mínimas de captura por tipo de animal.
 * Permite ver y editar especies según su tipo (pez, molusco, etc.), incluyendo su nombre común, científico,
 * talla mínima y una imagen asociada.
 */
@Component({
  selector: 'app-tabla-medidas',
  templateUrl: './tabla-medidas.page.html',
  styleUrls: ['./tabla-medidas.page.scss'],
  standalone: true,
  imports: [IonText, 
    IonContent, IonHeader, IonTitle, IonToolbar, CommonModule,
    FormsModule, ReactiveFormsModule, IonButtons, IonButton, IonFooter,
    IonCard, IonCardContent, IonCardTitle, IonCardHeader, IonList,
    IonLabel, IonItem, IonInput, IonCardSubtitle, IonImg, IonSelectOption,
    IonSelect, IonSearchbar,IonIcon,IonText
  ]
})
export class TablaMedidasPage implements OnInit {
    /** Inyección de dependencias necesarias para servicios y navegación */
  private router: Router = inject(Router);
  private fb: FormBuilder = inject(FormBuilder);
  private medidaService: MedidaService = inject(MedidaService);
  private toastController = inject(ToastController);
  private alertController = inject(AlertController);

  
 /** Variables de estado y control del formulario y edición */
  busqueda: string = '';
  tipoSeleccionado: number = 1;
  modoEdicion = false;
  medidaEdicion: number | null = null;

  /** Variables para nueva medida o edición */
  nuevoNombre: string = '';
  nuevoNombreCientifico: string = '';
  nuevaTallaMinima: string = '';
  nuevaImagen: string = '';
  imagenSeleccionada!: File;
  imagenPreviewUrl: string | null = null;

  /** Formulario reactivo para crear o editar medidas */
  formMedida!: FormGroup;

  /** Control de roles */
  esAdmin: boolean = false;

  /** Datos cargados desde el backend */
  tiposAnimales: TipoAnimal[] = [];
  especies: Medida[] = [];
  especiesFiltradasList: Medida[] = [];


   /**
   * Constructor: registra iconos a utilizar en la interfaz
   */
  constructor() {
          addIcons({
                          'search-outline': searchOutline,
                          'calendar-outline': calendarOutline,
                          'images-outline': imagesOutline,
                          'close-circle':closeCircle
                        });
                      }
  

  /**
   * init de inicialización del componente
   */
  ngOnInit() {
    this.esAdmin = this.verificarAdmin();
    this.cargarTipos();
    this.cargarMedidas();
  }

    /**
   * Carga los tipos de animales disponibles para clasificación
   */
  async cargarTipos() {
    try {
      this.tiposAnimales = await this.medidaService.getTipos();
      console.log('Tipos de animales cargados:', this.tiposAnimales);
    } catch (error) {
      console.error('Error al obtener los tipos de animales:', error);
    }
  }
  /**
   * Carga todas las medidas desde el servicio y aplica el filtro inicial
   */
  async cargarMedidas() {
    try {
      const medidas = await this.medidaService.getMedidas();
      this.especies = Array.isArray(medidas) ? medidas : [];
      
      this.filtrasEspecies();
    } catch (error) {
      console.error('Error al obtener las medidas:', error);
    }

    this.formMedida = this.fb.group({
      nombre: ['', Validators.required],
      nombreCientifico: ['', Validators.required],
      tallaMinima: ['', Validators.required],
      imagen: ['', Validators.required],
      tipo: [1, Validators.required],
    });
  }
  /**
   * Filtra la lista de especies según el tipo seleccionado y el texto buscado
   */
  filtrasEspecies() {
    const texto = this.busqueda.trim().toLowerCase();
    this.especiesFiltradasList = this.especies.filter(especie =>
      especie.idTipo === this.tipoSeleccionado &&
      especie.nombreComun.toLowerCase().includes(texto)
    );
  }
  /**
   * Procesa la imagen seleccionada por el usuario y genera una vista previa
   */
    onImagenSeleccionada(event: any) {
    let file = event.target.files[0];
    if (file) {
      this.imagenSeleccionada = file;

      const reader = new FileReader();
      reader.onload = () => {
        this.imagenPreviewUrl = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  /**
   * Crea una nueva medida con la información del formulario y la imagen
   */
  async crearNuevaMedida() {
    if (!this.imagenSeleccionada) {
      console.error('No se ha seleccionado imagen');
      return;
    }

    try {
      await this.medidaService.subirImagenMedida(this.imagenSeleccionada).then((response)=>{
        this.mostrarMensaje('Imagen subida exitosamente', 'success');

      }).catch((error)=>{ 
        console.error('Error al subir la imagen:', error);
        this.mostrarMensaje('Error al subir la imagen', 'danger');
      });

      const form = this.formMedida.value;
      const nuevaMedida: Medida = {
        idMedida: 0,
        nombreComun: form.nombre,
        nombreCientifico: form.nombreCientifico,
        tallaMinima: form.tallaMinima,
        imagen: this.imagenSeleccionada.name,
        idTipo: form.tipo
      };

      const mensaje = await this.medidaService.crearMedida(nuevaMedida).then((response)=>{
        this.mostrarMensaje('Medida creada exitosamente', 'success');
        console.log('Medida creada:', nuevaMedida);
        return response;
      }).catch((error)=>{
        console.error('Error al crear la medida:', error);
        this.mostrarMensaje('Error al crear la medida', 'danger');
        throw error; 
      });
      this.cargarMedidas();
      this.cancelarEdicion();
    } catch (error) {
      console.error('Error al crear la medida:', error);
      this.mostrarMensaje('Error al crear la medida', 'danger');
    }
  }

  /**
   * Guarda los cambios en una medida existente, incluyendo una nueva imagen si se seleccionó
   */
  async guardarCambios(especie: Medida) {
  try {
    const medidaActualizada: Medida = {
      ...especie, 
      nombreComun: this.nuevoNombre?.trim() || especie.nombreComun,
      nombreCientifico: this.nuevoNombreCientifico?.trim() || especie.nombreCientifico,
      tallaMinima: this.nuevaTallaMinima?.trim() || especie.tallaMinima,
      imagen: especie.imagen, 
      idTipo: this.tipoSeleccionado || especie.idTipo
    };

    if (this.imagenSeleccionada) {
      await this.medidaService.subirImagenMedida(this.imagenSeleccionada);
      medidaActualizada.imagen = this.imagenSeleccionada.name;
    }

    const mensaje = await this.medidaService.EditarMedida(medidaActualizada);
    this.mostrarMensaje(mensaje, 'success');
    this.cancelarEdicion();
    this.cargarMedidas();

  } catch (error) {
    console.error('Error al guardar cambios:', error);
    this.mostrarMensaje('Error al guardar los cambios', 'danger');
  }
}

  /**
   * Activa el modo de edición con los datos de una especie
   */
  activarEdicion(especie: Medida) {
  this.modoEdicion = true;
  this.medidaEdicion = especie.idMedida;

  this.nuevoNombre = especie.nombreComun;
  this.nuevoNombreCientifico = especie.nombreCientifico;
  this.nuevaTallaMinima = especie.tallaMinima;
  this.tipoSeleccionado = especie.idTipo;

  this.imagenPreviewUrl = this.getImagenUrl(especie.imagen);
  this.imagenSeleccionada = null as any;
}

  /**
   * Cancela el modo de edición y resetea el formulario
   */
  cancelarEdicion() {
    this.modoEdicion = false;
    this.medidaEdicion = null;
    this.formMedida.reset();
  }
  /**
   * Construye la URL completa para acceder a una imagen de medida desde el backend
   */
  getImagenUrl(nombre: string): string {
    return `${environment.api}/medida/imagen/${nombre}`;
  }
  /**
   * Verifica si el usuario actual es administrador según el JWT
   */
  verificarAdmin(): boolean {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
      return false;
    }
    try {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.rol === 'ADMIN';
    } catch (error) {
      this.router.navigate(['/login']);
      return false;
    }
  }
  /**
   * Solicita confirmación al usuario y elimina una medida si acepta
   */
  async eliminarMedida(especie: Medida) {
    const alert = await this.alertController.create({
      header: 'Confirmar eliminación',
      message: `¿Estás seguro de que deseas eliminar ${especie.nombreComun}?`,
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
              const mensaje = await this.medidaService.EliminarMedida(especie);
              this.especies = this.especies.filter(m => m.idMedida !== especie.idMedida);
              this.filtrasEspecies();
              this.mostrarMensaje(mensaje, 'success');
            } catch (error) {
              console.error('Error al eliminar la medida:', error);
              this.mostrarMensaje('Error al eliminar la medida', 'danger');
            }
          }
        }
      ]
    });

    await alert.present();
  }
  /**
   * Muestra un mensaje tipo toast en pantalla con un color específico
   */
  async mostrarMensaje(mensaje: string, color: 'success' | 'danger') {
    const toast = await this.toastController.create({
      message: mensaje,
      duration: 3000,
      position: 'top',
      color: color
    });
    await toast.present();
  }

}