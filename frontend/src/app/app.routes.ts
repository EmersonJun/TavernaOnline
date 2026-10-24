import { Routes } from '@angular/router';
import { autenticadoGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'campanhas' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'cadastro',
    loadComponent: () => import('./features/auth/cadastro.component').then((m) => m.CadastroComponent)
  },
  {
    path: 'campanhas',
    canActivate: [autenticadoGuard],
    loadComponent: () => import('./features/campanhas/campanhas.component').then((m) => m.CampanhasComponent)
  },
  {
    path: 'campanhas/:campanhaId/personagens',
    canActivate: [autenticadoGuard],
    loadComponent: () => import('./features/personagens/ficha.component').then((m) => m.FichaComponent)
  },
  {
    path: 'campanhas/:campanhaId/diario',
    canActivate: [autenticadoGuard],
    loadComponent: () => import('./features/diario/diario.component').then((m) => m.DiarioComponent)
  },
  {
    path: 'sessoes/:sessaoId/sala',
    canActivate: [autenticadoGuard],
    loadComponent: () => import('./features/sessao/sala.component').then((m) => m.SalaComponent)
  },
  { path: '**', redirectTo: 'campanhas' }
];
