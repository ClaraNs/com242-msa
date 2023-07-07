import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DashCoordenadorPageRoutingModule } from './dash-coordenador-routing.module';

import { DashCoordenadorPage } from './dash-coordenador.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    DashCoordenadorPageRoutingModule
  ],
  declarations: [DashCoordenadorPage]
})
export class DashCoordenadorPageModule {}
