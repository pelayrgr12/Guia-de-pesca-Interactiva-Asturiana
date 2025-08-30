import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { IonButton, IonContent, IonHeader, IonItem, IonLabel, IonTitle, IonToolbar, IonInput, IonIcon, ToastController, IonCardHeader, IonCardTitle } from '@ionic/angular/standalone';
import { HttpClient } from '@angular/common/http';
import { RegistroService } from 'src/app/shared/services/registro.service';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { addIcons } from 'ionicons';
import { eye, eyeOff } from 'ionicons/icons';
import { Usuario } from 'src/app/shared/interfaces/Usuario';
import { UsuariosService } from 'src/app/shared/services/usuarios.service';

/**
 * Página de registro de usuarios.
 * Permite al usuario crear una cuenta introduciendo sus datos personales y una contraseña válida.
 */
@Component({
  selector: 'app-registro',
  templateUrl: './registro.page.html',
  styleUrls: ['./registro.page.scss'],
  standalone: true,
  imports: [IonIcon, IonInput, IonContent, IonHeader, IonTitle,
    IonToolbar, CommonModule, FormsModule,
    IonItem, IonLabel, IonButton, ReactiveFormsModule, IonCardHeader, IonCardTitle]
})
export class RegistroPage implements OnInit {

  /** Cliente HTTP para peticiones */
  private httpClient: HttpClient = inject(HttpClient);
  /** Servicio encargado del registro de usuarios */
  private registroService: RegistroService = inject(RegistroService);
  /** Servicio para operaciones relacionadas con usuarios */
  private usuariosService: UsuariosService = inject(UsuariosService);
  /** Enrutador para navegar entre páginas */
  private router: Router = inject(Router);
  /** Controlador de toasts (mensajes flotantes) */
  private toastController = inject(ToastController);
  /** Formulario reactivo de registro */
  registroForm: FormGroup;

  /**
   * Constructor: inicializa el formulario con validaciones y registra los iconos de visibilidad.
   */
  constructor(private fb: FormBuilder) {
    this.registroForm = this.fb.group({
      nombre: [null, [Validators.required]],
      contrasena: [null, [Validators.required, Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[ !"·$%&/()=?¿^*+´{}[\]\\\-_:;,.<>@|#~]).{8,}$/)
      ]],
      correo: [null, [Validators.required, Validators.email]],
      confirmarContrasena: [null, [Validators.required]],
      fechaNacimiento: [null, [Validators.required, this.validarFechaValida]]
    });
    addIcons({
      'eye': eye,
      'eye-off': eyeOff
    });
  }

  /**
   * Validador personalizado para comprobar si una fecha es válida y está en el rango permitido.
   */
  validarFechaValida(control: AbstractControl): ValidationErrors | null {
    const fecha = new Date(control.value);
    const hoy = new Date();
    const anioMinimo = 1900;

    if (isNaN(fecha.getTime())) {
      return { fechaInvalida: true }; 
    }

    if (fecha.getFullYear() < anioMinimo || fecha > hoy) {
      return { fueraDeRango: true }; 
    }

    return null; 
  }

  /**
   * Muestra un toast con un mensaje y color determinado.
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
   * Hook de inicialización del componente.
   * Si el usuario ya ha iniciado sesión, redirige al menú.
   */
  async ngOnInit() {
    if(this.obtenidoLogin()){
      this.router.navigate(['/menu']);
    }
  }

  /**
   * Comprueba si hay un token de sesión activo.
   */
  obtenidoLogin(): boolean {
    const token = localStorage.getItem('token');
    if (token)
      return true;
    else
      return false;
  }

  /**
   * Redirige a la página de login.
   */
  navLogin() {
    this.router.navigate(['/login']);
  }

  /**
   * Envía el formulario de registro si es válido.
   */
  registrar() {
    if (this.registroForm.invalid) {
      this.mostrarMensaje('Formulario incompleto o inválido', 'danger');
      return;
    }

    if (this.validarPassword()) {
      this.mostrarMensaje('Las contraseñas no coinciden', 'danger');
      return;
    }

    if (this.validarPassword()) {
      return;
    }

    this.validarPassword();
    const usuarioRegistro = this.registroForm.value;

    this.registroService.registro(usuarioRegistro).subscribe({
      next:(data)=>{
        this.router.navigate(['/login']);
        this.mostrarMensaje('Registro exitoso', 'success');
      }, error:(error)=>{
        this.mostrarMensaje('Error en el registro', 'danger');
      }
    })
  }

  /** Bandera de validación de contraseñas */
  validar: boolean = false;

  /**
   * Comprueba si la contraseña coincide con la confirmación.
   */
  validarPassword() {
    let password = this.registroForm.get('contrasena')?.value;
    let confirmarPassword = this.registroForm.get('confirmarContrasena')?.value;
    if (password !== confirmarPassword) {
       return this.validar = true; 
    } else {
      return this.validar = false; 
    }
  }

  /** Controla la visibilidad del campo contraseña */
  verContrasena = false;

  /**
   * Alterna la visibilidad de la contraseña.
   */
  toggleContrasenaVisibility() {
    this.verContrasena = !this.verContrasena;
  }

  /** Controla la visibilidad del campo de confirmación de contraseña */
  verConfirmarContrasena = false;

  /**
   * Alterna la visibilidad de la confirmación de contraseña.
   */
  toggleConfirmarContrasenaVisibility() {
    this.verConfirmarContrasena = !this.verConfirmarContrasena;
  }
}
