import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./home/home.module').then( m => m.HomePageModule)
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'envia-arquivo-discente',
    loadChildren: () => import('./envia-arquivo-discente/envia-arquivo-discente.module').then( m => m.EnviaArquivoDiscentePageModule)
  },
  {
    path: 'dash-docente',
    loadChildren: () => import('./dash-docente/dash-docente.module').then( m => m.DashDocentePageModule)
  },
  {
    path: 'dash-coordenador',
    loadChildren: () => import('./dash-coordenador/dash-coordenador.module').then( m => m.DashCoordenadorPageModule)
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
