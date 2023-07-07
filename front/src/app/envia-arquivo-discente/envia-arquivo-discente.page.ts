import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ShareService } from '../services/share.service';
import { IonModal, ToastController } from '@ionic/angular';

@Component({
  selector: 'app-envia-arquivo-discente',
  templateUrl: './envia-arquivo-discente.page.html',
  styleUrls: ['./envia-arquivo-discente.page.scss', '../../global.css'],
})
export class EnviaArquivoDiscentePage implements OnInit {

  @ViewChild(IonModal) modal: IonModal | undefined;
  envioAtivo: any =  false;
  selectedFile: File | undefined;
  matricula: any = "";

  formFields = {
    titulo: '',
    resumo: '',
    matriculaOrientador: '',
    enviadopor: ''
  };

  isModalOpen = false;
  isModalOpenInfo = false;
  artigos: any = [];
  artigoAtual:any;
  selectedFileCorrecao: any;
  loading = false;
  aluno:any;

  constructor(private route: ActivatedRoute, private router: Router, private share: ShareService, private toastCtrl: ToastController) { 

  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (this.router.getCurrentNavigation()?.extras.state != null) {
        let dados: any = this.router.getCurrentNavigation()?.extras?.state;
        this.aluno = dados;
        this.formFields.enviadopor = dados.matricula;
        this.matricula = dados.matricula;
        this.carregaArtigos();
      }
    });

  }

  abrirInfo(art: any){
    this.artigoAtual = art;
    this.abrirModalInfo(true);
  }

  carregaArtigos(){
    
    this.share.retornaArtigosPorMatricula(this.matricula).subscribe((data:any)=>{
      this.loading = true;
      this.artigos = [];
      console.log(data);
      if(data.length == 0){
        this.envioAtivo = true;
      }else{
      data.forEach((x: any) =>{
        let art = {
          idartigo: x.idArtigo,
          titulo: x.titulo,
          url: x.url,
          resumo: x.resumo,
          dataEnvio: x.dataEnvio,
          alteracao: x.alteracao,
          status: x.status,
          notaFinal: x.notaFinal == -1 ? 0 : x.notaFinal,
          consideracoes: x.consideracoes,
          orientador: x.orientador
        };
        this.artigos.push(art);

      });
      console.log(this.artigos);
      
    }
    this.loading = false;
    });
  }

  correcao(art: any){
    this.artigoAtual = art;
    this.abrirModal(true);
  }

  baixarArquivo(url:string) {
    window.open(`http://localhost:8081${url}`, '_blank');
  }

  fechar() {
    this.modal?.dismiss(null, 'cancel');
    this.isModalOpen = false;
    this.isModalOpenInfo = false;
  }

  abrirModal(isOpen: boolean) {
    this.isModalOpen = isOpen;
  }

  abrirModalInfo(isOpen: boolean) {
    this.isModalOpenInfo = isOpen;
  }


  onWillDismiss(event:any) {
    this.isModalOpen = false;
    this.carregaArtigos();
  }

  onWillDismissInfo(event:any) {
    this.isModalOpenInfo = false;
  }

  enviarFormulario() {
    if (this.selectedFile) {
      this.share.uploadArtigo(this.selectedFile, this.formFields).subscribe((response) => {
        if(response){
          this.presentToast('Artigo enviado com sucesso!');
          this.carregaArtigos();
        }else{
          this.presentToast('Algo deu errado...');
        }
      });
    }
  }

  enviarCorrecao() {
    if (this.selectedFileCorrecao) {
      this.share.reenviarArtigo(this.selectedFileCorrecao, this.artigoAtual.idartigo).subscribe((response) => {
        if(response){
          this.presentToast('Artigo atualizado com sucesso!');
          this.carregaArtigos();
          this.fechar();
        }else{
          this.presentToast('Algo deu errado...');
        }
      });
    }
  }
  

  async presentToast(msg:string) {
    const toast = await this.toastCtrl.create({
      message: msg,
      duration: 1500,
      position: 'bottom',
    });

    await toast.present();
  }
  
  onFileChange(event: any) {
    const fileList: FileList = event.target.files;
    if (fileList && fileList.length > 0) {
      this.selectedFile = fileList[0];
    }
  }

  onFileChangeCorrecao(event: any) {
    const fileList: FileList = event.target.files;
    if (fileList && fileList.length > 0) {
      this.selectedFileCorrecao = fileList[0];
    }
  }


}
