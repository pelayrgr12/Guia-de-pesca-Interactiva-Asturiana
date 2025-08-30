import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonCard, IonCardHeader, IonCardTitle, IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';
import { Router } from '@angular/router';

@Component({
  selector: 'app-inicio',
  templateUrl: './inicio.page.html',
  styleUrls: ['./inicio.page.scss'],
  standalone: true,
  imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule,IonCardHeader,IonCardTitle]
})
export class InicioPage implements OnInit {

  /**
   * Inyección del router para navegar entre rutas.
   */
  private router:Router = inject(Router);

  constructor() { }

  /**
   * Método del ciclo de vida que se ejecuta al iniciar el componente.
   */
  ngOnInit() {
  }

  /**
   * Navega a la página de login.
   */
  navLogin(){
    this.router.navigate(['/login']);
  }

}
