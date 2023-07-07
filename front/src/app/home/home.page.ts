import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ShareService } from '../services/share.service';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss', '../../global.css'],
})
export class HomePage {
  matricula: any = null;
  senha: any = null;
  tipomatricula: any = 'aluno';

  constructor(private router: Router, private share: ShareService, private toastCtrl: ToastController) { }


  doLogin() {
    // Aqui você pode adicionar a lógica de autenticação

    let params = {
      matricula: this.matricula,
      nome: "",
      email: "",
      coordenador: false,
    };


    if (this.tipomatricula == "aluno") {
      this.share.getAlunoPorMatricula(this.matricula).subscribe((data: any) => {
        if (data != null) {
          params.nome = data.nome;
          params.email = data.email;

          this.router.navigate(['/envia-arquivo-discente'], { state: params });
        } else {
          this.presentToast("Não foi encontrado cadastro com esta matrícula.")
        }
      });

    } else {
      this.share.getProfPorMatricula(this.matricula).subscribe((data: any) => {
        console.log(data);
        if (data != null) {
          params.nome = data.nome;
          params.email = data.email;
          params.coordenador = data.coordenador;
          console.log(data);

          if (data.coordenador == false) {
            this.router.navigate(['/dash-docente'], { state: params });
          } else {
            this.router.navigate(['/dash-coordenador'], { state: params });
          }
        } else {
          this.presentToast("Não foi encontrado cadastro com esta matrícula.")
        }
      });
    }
  }

  async presentToast(msg: string) {
    const toast = await this.toastCtrl.create({
      message: msg,
      duration: 1500,
      position: 'bottom',
    });

    await toast.present();
  }
}
