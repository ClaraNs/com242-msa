import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DashDocentePage } from './dash-docente.page';

const routes: Routes = [
  {
    path: '',
    component: DashDocentePage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashDocentePageRoutingModule {}
