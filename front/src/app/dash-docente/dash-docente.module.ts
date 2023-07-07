import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DashDocentePageRoutingModule } from './dash-docente-routing.module';

import { DashDocentePage } from './dash-docente.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    DashDocentePageRoutingModule
  ],
  declarations: [DashDocentePage]
})
export class DashDocentePageModule {}
