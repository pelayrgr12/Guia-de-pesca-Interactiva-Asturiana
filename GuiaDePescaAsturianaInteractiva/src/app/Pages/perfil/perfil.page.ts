import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import {
  IonButton, IonButtons, IonCard, IonCardContent, IonCardHeader,
  IonCardTitle, IonContent, IonFooter, IonHeader, IonSpinner,
  IonTitle, IonToolbar, IonInput, IonItem, IonLabel, IonIcon,
  ToastController
} from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { Usuario } from 'src/app/shared/interfaces/Usuario';
import { UsuariosService } from 'src/app/shared/services/usuarios.service';
import { jwtDecode } from 'jwt-decode';
import { PerfilService } from 'src/app/shared/services/perfil.service';
import { addIcons } from 'ionicons';
import { eye, eyeOff } from 'ionicons/icons';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.page.html',
  styleUrls: ['./perfil.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule,
    IonContent, IonHeader, IonTitle, IonToolbar,
    IonFooter, IonButtons, IonButton, IonCard,
    IonCardHeader, IonCardTitle, IonCardContent,
    IonSpinner, IonInput, IonItem, IonLabel, IonIcon
  ]
})
export class PerfilPage implements OnInit {

  private router: Router = inject(Router);
  private usuarioService = inject(UsuariosService);
  private toastController = inject(ToastController);
  private perfilService = inject(PerfilService);

  /** Datos del usuario actual */
  usuario?: Usuario;

  /** Estado para activar el modo edición del perfil */
  modoEdicion = false;

  /** Formulario reactivo para edición de perfil */
  formPerfil!: FormGroup;

  /** Estado para mostrar u ocultar contraseña actual */
  verActual = false;

  /** Estado para mostrar u ocultar nueva contraseña */
  verNueva = false;

  /** Estado para mostrar u ocultar confirmación de nueva contraseña */
  verConfirmarNueva = false;

  /** Flag para indicar si hubo errores de validación */
  validar = false;

  /** Flag para mostrar campos de cambio de contraseña */
  cambioPassword: boolean = false;

  /** Determina si el usuario es administrador */
  esAdmin: boolean = false;

  constructor(private fb: FormBuilder) {
    addIcons({
      'eye': eye,
      'eye-off': eyeOff
    });
  }

  /**
   * Inicializa la vista, obtiene los datos del usuario y prepara el formulario.
   */
  async ngOnInit() {
    this.esAdmin = this.verificarAdmin();
    try {
      this.usuario = await this.usuarioService.getUsuario();

      this.formPerfil = this.fb.group({
        nombre: [this.usuario?.nombre, Validators.required],
        correo: [this.usuario?.correo, [Validators.required, Validators.email]],
        fechaNacimiento: [this.usuario?.fechaNacimiento, Validators.required],
        contrasenaActual: ['', Validators.required],
        nuevaContrasena: ['', [
          Validators.minLength(8),
          Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[ !"·$%&/()=?¿^*+´{}[\]\\\-_:;,.<>@|#~]).{8,}$/)
        ]],
        confirmarContrasena: ['']
      });

    } catch (error) {
      console.error('Error al obtener el usuario', error);
    }
  }

  /**
   * Activa la edición del perfil y carga valores actuales en el formulario.
   */
  activarEdicion() {
    this.modoEdicion = true;

    const fecha = this.usuario?.fechaNacimiento
      ? this.formatearFechaInput(this.usuario.fechaNacimiento)
      : '';

    this.formPerfil.patchValue({
      nombre: this.usuario?.nombre,
      correo: this.usuario?.correo,
      fechaNacimiento: fecha
    });
  }

  /**
   * Formatea una fecha en formato YYYY-MM-DD para el input
   */
  formatearFechaInput(fecha: string | Date): string {
    const d = new Date(fecha);
    const year = d.getFullYear();
    const month = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Cancela la edición del perfil y restaura el formulario
   */
  cancelarEdicion() {
    this.modoEdicion = false;
    this.validar = false;
    this.cambioPassword = false;
    this.formPerfil.reset({
      nombre: this.usuario?.nombre,
      correo: this.usuario?.correo,
      fechaNacimiento: this.usuario?.fechaNacimiento
    });
  }

  /**
   * Guarda los cambios del perfil, incluyendo contraseña si corresponde
   */
  async guardarCambios() {
    const nueva = this.formPerfil.get('nuevaContrasena')?.value?.trim();
    const confirmar = this.formPerfil.get('confirmarContrasena')?.value?.trim();
    const actual = this.formPerfil.get('contrasenaActual')?.value?.trim();

    if (!actual) {
      this.validar = true;
      this.mostrarMensaje('Debes introducir la contraseña actual para confirmar los cambios', 'danger');
      return;
    }

    const quiereCambiarContrasena = nueva || confirmar;

    if (quiereCambiarContrasena) {
      if (!nueva || !confirmar || nueva !== confirmar) {
        this.validar = true;
        this.mostrarMensaje('Las contraseñas no coinciden', 'danger');
        return;
      }
    }

    this.validar = false;

    const dto: any = {
      nombre: this.formPerfil.value.nombre,
      correo: this.formPerfil.value.correo,
      fechaNacimiento: this.formPerfil.value.fechaNacimiento,
      contrasenaActual: actual
    };

    if (quiereCambiarContrasena) {
      dto.nuevaContrasena = nueva;
    }

    try {
      await this.perfilService.actualizarPerfil(dto);

      if (dto.correo !== this.usuario?.correo) {
        this.mostrarMensaje('Se ha cambiado el correo, por favor inicia sesión de nuevo', 'success');
        this.cerrarSesion();
        this.formPerfil.reset(this.usuario);
        this.formPerfil.patchValue({
          contrasenaActual: '',
          nuevaContrasena: '',
          confirmarContrasena: ''
        });
        return;
      }

      this.mostrarMensaje('Perfil actualizado correctamente', 'success');
      this.usuario = await this.usuarioService.getUsuario();
      this.formPerfil.reset(this.usuario);
      this.formPerfil.patchValue({
        contrasenaActual: '',
        nuevaContrasena: '',
        confirmarContrasena: ''
      });

      this.modoEdicion = false;
    } catch (error: any) {
      let mensaje = 'Error al actualizar el perfil';

      if (error.status === 400 || error.status === 401) {
        if (typeof error.body === 'string') {
          mensaje = error.body;
        } else if (error.body?.mensaje) {
          mensaje = error.body.mensaje;
        } else {
          mensaje = 'Contraseña actual incorrecta';
        }
      }

      this.mostrarMensaje(mensaje, 'danger');
      console.error(error);
    }
  }

  /**
   * Activa o desactiva los campos de cambio de contraseña
   */
  activarCambioContrasena() {
    this.cambioPassword = !this.cambioPassword;
    console.log(this.cambioPassword);
  }

  /**
   * Muestra un mensaje tipo toast en pantalla
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

  /** Alterna la visibilidad del campo de contraseña actual */
  toggleActualVisibility() {
    this.verActual = !this.verActual;
  }

  /** Alterna la visibilidad del campo de nueva contraseña */
  toggleNuevaVisibility() {
    this.verNueva = !this.verNueva;
  }

  /** Alterna la visibilidad del campo de confirmar nueva contraseña */
  toggleConfirmarNuevaVisibility() {
    this.verConfirmarNueva = !this.verConfirmarNueva;
  }

  /**
   * Verifica si el usuario es admin y redirige si no lo es
   */
  verificarAdmin(): boolean {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
      return false;
    }
    try {
      const decodedToken: any = jwtDecode(token);
      if (decodedToken.rol === 'USER') {
        return false;
      } else if (decodedToken.rol === 'ADMIN') {
        return true;
      } else {
        return false;
      }
    } catch (error) {
      this.router.navigate(['/login']);
      return false;
    }
  }

  /** Cierra sesión y redirige al login */
  cerrarSesion() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  /** Navega a la sección de gestión */
  navGestion() {
    this.router.navigate(['/tabs/gestion']);
  }
}
