import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { EnviaArquivoDiscentePageRoutingModule } from './envia-arquivo-discente-routing.module';

import { EnviaArquivoDiscentePage } from './envia-arquivo-discente.page';
import { FileChooser } from '@ionic-native/file-chooser/ngx';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    EnviaArquivoDiscentePageRoutingModule
  ],
  declarations: [EnviaArquivoDiscentePage],
  providers: [FileChooser]
})
export class EnviaArquivoDiscentePageModule {}
