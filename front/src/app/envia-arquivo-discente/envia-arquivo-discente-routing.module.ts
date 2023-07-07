import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { EnviaArquivoDiscentePage } from './envia-arquivo-discente.page';

const routes: Routes = [
  {
    path: '',
    component: EnviaArquivoDiscentePage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EnviaArquivoDiscentePageRoutingModule {}
