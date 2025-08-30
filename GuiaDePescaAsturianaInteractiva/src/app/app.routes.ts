import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'inicio',
    pathMatch: 'full',
  },
  {
    path: 'inicio',
    loadComponent: () =>
      import('./Pages/inicio/inicio.page').then((m) => m.InicioPage),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./Pages/login/login.page').then((m) => m.LoginPage),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./Pages/registro/registro.page').then((m) => m.RegistroPage),
  },

  {
    path: 'tabs',
    loadComponent: () =>
      import('./tabs/tabs.page').then((m) => m.TabsPage),
    canActivate: [AuthGuard],
    children: [
      {
        path: 'menu',
        loadComponent: () =>
          import('./Pages/menu/menu.page').then((m) => m.MenuPage),
      },
      {
        path: 'mapa',
        loadComponent: () =>
          import('./Pages/mapa/mapa.page').then((m) => m.MapaPage),
      },
      {
        path: 'mis-puntos',
        loadComponent: () =>
          import('./Pages/mis-puntos/mis-puntos.page').then((m) => m.MisPuntosPage),
      },
      {
        path: `historial/:idPunto`,
        loadComponent: () =>
          import('./Pages/historial/historial.page').then((m) => m.HistorialPage),
      },
      {
        path: 'tabla-medidas',
        loadComponent: () =>
          import('./Pages/tabla-medidas/tabla-medidas.page').then((m) => m.TablaMedidasPage),
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./Pages/perfil/perfil.page').then((m) => m.PerfilPage),
      },
      {
        path: 'gestion',
        loadComponent: () =>
          import('./Pages/gestion/gestion.page').then((m) => m.GestionPage),
      },
    ],
  },
];
