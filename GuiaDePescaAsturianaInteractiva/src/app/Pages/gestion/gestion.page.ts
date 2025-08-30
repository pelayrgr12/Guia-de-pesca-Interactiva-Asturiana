import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton,
  IonItem, IonLabel, IonList, IonFooter, IonButtons, IonSearchbar
} from '@ionic/angular/standalone';
import { UsuariosService } from 'src/app/shared/services/usuarios.service';
import { Usuario } from 'src/app/shared/interfaces/Usuario';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-gestion',
  templateUrl: './gestion.page.html',
  styleUrls: ['./gestion.page.scss'],
  standalone: true,
  imports: [
    IonSearchbar, IonFooter, IonContent, IonHeader,
    IonTitle, IonToolbar, IonButton, IonItem, IonLabel,
    IonList, CommonModule, FormsModule, IonFooter, IonButtons, IonSearchbar
  ]
})
export class GestionPage implements OnInit {

  /** Lista completa de usuarios obtenidos del backend */
  usuarios: Usuario[] = [];

  /** Lista de usuarios filtrada por la búsqueda */
  usuariosFiltrados: Usuario[] = [];

  /** Término de búsqueda introducido por el usuario */
  busqueda: string = "";

  /**
   * Constructor que inyecta el servicio de usuarios.
   * @param usuariosService Servicio para interactuar con los usuarios
   */
  constructor(private usuariosService: UsuariosService) {}

  /** Instancia del Router inyectada usando `inject()` */
  private router: Router = inject(Router);

  /**
   * Método que se ejecuta al iniciar la vista.
   * Obtiene los usuarios del backend, los filtra y verifica si el usuario es administrador.
   */
  async ngOnInit() {
    this.usuarios = await this.usuariosService.getUsuarios();
    this.filtrarUsuarios();
    this.verificarAdmin();
  }

  /**
   * Filtra la lista de usuarios por el nombre introducido en la búsqueda.
   */
  filtrarUsuarios() {
    const busqueda = this.busqueda.trim().toLowerCase();
    this.usuariosFiltrados = this.usuarios.filter(usuario =>
      usuario.nombre.trim().toLowerCase().includes(busqueda)
    );
  }

  /**
   * Cambia el estado de habilitado de un usuario (activar o desactivar).
   * @param usuario Usuario al que se le cambiará el estado
   */
  async toggleHabilitado(usuario: Usuario) {
    const nuevoEstado = !usuario.habilitado;
    await this.usuariosService.actualizarHabilitado(usuario.id, nuevoEstado);
    usuario.habilitado = nuevoEstado; 
  }

  /**
   * Verifica si el usuario autenticado es administrador.
   * Si no lo es, lo redirige a la ruta correspondiente.
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
      }
    } catch (error) {
      console.error('Error al decodificar token:', error);
      this.router.navigate(['/login']);
    }
  }
}
