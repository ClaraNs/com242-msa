import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DashCoordenadorPage } from './dash-coordenador.page';

const routes: Routes = [
  {
    path: '',
    component: DashCoordenadorPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashCoordenadorPageRoutingModule {}
