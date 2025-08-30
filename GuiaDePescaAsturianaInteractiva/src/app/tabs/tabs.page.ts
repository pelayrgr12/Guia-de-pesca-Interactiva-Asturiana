import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonButton, IonButtons, IonCardContent, IonCardTitle, IonCol, IonContent, 
  IonGrid, 
  IonHeader, IonIcon, IonInput, IonItem, IonLabel, IonRouterOutlet, IonRow, IonTabBar, 
  IonTabButton, IonTabs, IonTitle, IonToolbar } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { addIcons } from 'ionicons';
import { homeOutline, mapOutline,starOutline,
  calculatorOutline,personOutline,settingsOutline } from 'ionicons/icons';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.page.html',
  styleUrls: ['./tabs.page.scss'],
  standalone: true,
  imports: [IonLabel, IonItem,IonRouterOutlet,CommonModule,
     IonContent, IonHeader, IonTitle, IonToolbar,IonTabButton,IonTabs,IonTabBar
    , FormsModule, IonCardContent, IonButtons, IonButton, IonIcon, IonInput,IonCol,IonGrid,IonRow]
  
  
})
export class TabsPage implements OnInit {

  constructor() { 
    addIcons({
      'restaurant-outline': homeOutline,
      'map-outline': mapOutline,
      'star-outline': starOutline,
      'calculator-outline': calculatorOutline,
      'person-outline': personOutline,
      'settings-outline': settingsOutline,
    });
  }
  private router: Router = inject(Router);

  ngOnInit() {
    this.verificarAdmin();
    this.esAdmin = this.verificarAdmin();
  }

  esAdmin: boolean = false;

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
        }else{
          return false;
        }
      } catch (error) {
        this.router.navigate(['/login']);
        return false;
      }
    }
  
}
