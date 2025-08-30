import { Router } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  IonButton, IonContent, IonHeader, IonItem, IonLabel, IonTitle,
  IonToolbar, IonInput, ToastController, IonIcon, IonCardHeader, IonCardTitle
} from '@ionic/angular/standalone';
import { HttpClient } from '@angular/common/http';
import { UsuarioLogin } from 'src/app/shared/interfaces/usuario-login';
import { LoginService } from 'src/app/shared/services/login.service';
import { jwtDecode } from 'jwt-decode';
import { addIcons } from 'ionicons';
import { eye, eyeOff } from 'ionicons/icons';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [IonIcon, IonInput, IonContent, IonHeader, IonTitle,
    IonToolbar, CommonModule, FormsModule,
    IonItem, IonLabel, IonButton, CommonModule, ReactiveFormsModule, IonCardHeader, IonCardTitle]
})
export class LoginPage {

  /** Inyección del Router para navegación */
  private router: Router = inject(Router);

  /** Cliente HTTP para peticiones */
  private httpClient: HttpClient = inject(HttpClient);

  /** Servicio de login para autenticación */
  private loginService: LoginService = inject(LoginService);

  /** Controlador de toast para mostrar mensajes */
  private toastController = inject(ToastController);

  /** Formulario reactivo del login */
  userForm: FormGroup;

  /** Indica si el campo para recuperar contraseña está visible */
  mostrarInputRecuperar = false;

  /** Correo ingresado para recuperar contraseña */
  correo: string = '';

  /** Modelo de usuario para login */
  userLogin!: UsuarioLogin;

  /** Muestra u oculta la contraseña */
  verConfirmar = false;

  constructor(private fb: FormBuilder) {
    this.userForm = this.fb.group({
      correo: [null, [
        Validators.required,
        Validators.email
      ]],
      password: [null, [
        Validators.required,
      ]]
    });

    addIcons({
      'eye': eye,
      'eye-off': eyeOff
    });
  }

  /**
   * Alterna la visibilidad del campo de recuperación de contraseña.
   */
  mostrarEnlaceRecuperar() {
    this.mostrarInputRecuperar = !this.mostrarInputRecuperar;
  }

  /**
   * Envía una solicitud para recuperar la contraseña del usuario.
   */
  async recuperarPassword() {
    if (!this.correo || !this.correo.includes('@')) {
      this.mostrarMensaje('Por favor introduce un correo válido.', 'danger');
      return;
    }

    this.loginService.recuperarPassword(this.correo).subscribe({
      next: (mensaje) => {
        console.log(mensaje);
        this.mostrarMensaje(`Correo enviado a ${this.correo}`, 'success');
        this.correo = '';
        this.mostrarInputRecuperar = false;
      },
      error: (error) => {
        if (error.status === 404) {
          this.mostrarMensaje('Correo no encontrado', 'danger');
        } else {
          this.mostrarMensaje('Error al intentar recuperar la contraseña', 'danger');
        }
      }
    });
  }

  /**
   * Se ejecuta cuando la vista está a punto de entrar.
   * Si el usuario ya está logeado, se redirige al menú.
   */
  ionViewWillEnter() {
    if (this.obtenidoLogin()) {
      this.router.navigate(['tabs', 'menu']);
    }
  }

  /**
   * Comprueba si hay un token de sesión almacenado.
   * @returns `true` si hay token, `false` en caso contrario
   */
  obtenidoLogin(): boolean {
    const token = localStorage.getItem('token');
    return !!token;
  }

  /**
   * Navega a la página de registro.
   */
  navRegistro() {
    this.router.navigate(['/registro']);
  }

  /**
   * Envía la solicitud de login con el formulario actual.
   */
  async login() {
    this.userLogin = this.userForm.value;

    try {
      const response = await this.loginService.login(this.userLogin);
      const data = response.data;

      if (!data || !data.token) {
        this.mostrarMensaje('Datos incorrectos', 'danger');
        return;
      }

      this.desencriptarToken(data.token);
      this.userForm.reset();
      this.router.navigate(['/tabs/menu']);

    } catch (error) {
      console.error("Error en la solicitud:", error);
      this.mostrarMensaje(`No se pudo conectar con el servidor`, 'danger');
    }
  }

  /**
   * Almacena el token recibido en localStorage.
   * @param token JWT recibido tras login
   */
  desencriptarToken(token: string) {
    localStorage.setItem('token', token);
  }

  /**
   * Muestra un mensaje emergente tipo toast.
   * @param mensaje Mensaje a mostrar
   * @param color Color del toast ('success' o 'danger')
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
   * Alterna la visibilidad del campo de confirmación de contraseña.
   */
  toggleConfirmarVisibility() {
    this.verConfirmar = !this.verConfirmar;
  }
}
