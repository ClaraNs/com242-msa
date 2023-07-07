import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonModal, ToastController } from '@ionic/angular';
import { ShareService } from '../services/share.service';

@Component({
  selector: 'app-dash-coordenador',
  templateUrl: './dash-coordenador.page.html',
  styleUrls: ['./dash-coordenador.page.scss', '../../global.css'],
})
export class DashCoordenadorPage implements OnInit {

  @ViewChild(IonModal) modal: IonModal | undefined;
  artigoAtual: any;
  artigos:any = [];
  loading:any = true;
  isModalOpen: boolean = false;
  isModalOpenInfo: boolean = false;
  isModalOpenInfoBanca: boolean = false;
  matricula: any;
  consideracoes:any;
  correcao:boolean = false;
  prof:any;
  bancas:any = [];
  loadingBanca:any = true;
  bancasPendente:any = [];
  loadingBancaPendente:any = true;
  bancaAtual:any;


  constructor(private route: ActivatedRoute, private router: Router, private share: ShareService, private toastCtrl: ToastController) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (this.router.getCurrentNavigation()?.extras.state != null) {
        let dados: any = this.router.getCurrentNavigation()?.extras?.state;
        this.prof = dados;
        this.matricula = dados.matricula;
        this.carregaArtigos();
        this.carregaBancas();
        this.carregaBancasPendentes();
      }
    });
  }

  baixarArquivo(url:string) {
    window.open(`http://localhost:8081${url}`, '_blank');
  }

  fechar() {
    this.modal?.dismiss(null, 'cancel');
    this.isModalOpen = false;
    this.isModalOpenInfo = false;
    this.isModalOpenInfoBanca = false;
  }

  abrirModal(isOpen: boolean) {
    this.isModalOpen = isOpen;
  }

  abrirModalInfo(isOpen: boolean) {
    this.isModalOpenInfo = isOpen;
  }

  abrirModalInfoBanca(isOpen: boolean) {
    this.isModalOpenInfoBanca = isOpen;
  }

  onWillDismiss(event:any) {
    this.isModalOpen = false;
    this.carregaArtigos();
  }

  onWillDismissInfo(event:any) {
    this.isModalOpenInfo = false;
  }

  onWillDismissInfoBanca(event:any) {
    this.isModalOpenInfoBanca = false;
    this.carregaBancas();
  }

  carregaArtigos(){
    this.loading = true;
    this.share.retornaArtigos().subscribe((data:any)=>{
      this.loading = true;
      this.artigos = [];
      console.log(data);
      data.forEach((x: any) =>{
        let art = {
          idartigo: x.idArtigo,
          titulo: x.titulo,
          url: x.url,
          resumo: x.resumo,
          dataEnvio: x.dataEnvio,
          alteracao: x.alteracao,
          status: x.status,
          enviadoPor: x.enviadoPor,
          notaFinal: x.notaFinal == -1 ? 0 : x.notaFinal,
          consideracoes: x.consideracoes,
          matriculaOrientados: x.matriculaOrientador
        };
        this.artigos.push(art);
      });
      
    this.loading = false;
    });
  }

  carregaBancas(){
    this.loadingBanca = true;
    this.share.retornaBancas().subscribe((data:any)=>{
      this.bancas = [];
      console.log(data);
      data.forEach((x:any)=>{
        let banca = {
          idbanca: x.idBanca,
          dataRegistro: x.dataRegistro,
          dataAtualizacao: x.dataAtualizacao,
          dataAvaliacao: x.dataAvaliacao,
          artigoAvaliado: x.artigoAvaliado,
          status: x.status,
          correcao: x.correcao,
          aprovacao: false
        }

        this.bancas.push(banca);
      });
      this.loadingBanca = false;
    });
  }

  carregaBancasPendentes(){
    this.loadingBancaPendente = true;
    this.share.retornaBancasPendentes().subscribe((data:any)=>{
      this.bancasPendente = [];
      console.log(data);
      data.forEach((x:any)=>{
        let banca = {
          idbanca: x.idBanca,
          dataRegistro: x.dataRegistro,
          dataAtualizacao: x.dataAtualizacao,
          dataAvaliacao: x.dataAvaliacao,
          artigoAvaliado: x.artigoAvaliado,
          status: x.status,
          correcao: x.correcao,
          aprovacao: false
        }

        this.bancasPendente.push(banca);
      });
      this.loadingBancaPendente = false;
    });
  }

  verificarBanca(banca:any, aprovacao: boolean){
    this.share.autorizarBanca(banca.idbanca, aprovacao).subscribe((data:any)=>{
        this.presentToast("Banca verificada com sucesso.");
        this.carregaBancasPendentes();
        this.carregaBancas();
    });
  }


  abrirInfo(art: any){
    this.artigoAtual = art;
    this.abrirModalInfo(true);
  }

  infoBanca(banca:any){
    this.bancaAtual = banca;
    this.abrirModalInfoBanca(true);
  }


  async presentToast(msg:string) {
    const toast = await this.toastCtrl.create({
      message: msg,
      duration: 1500,
      position: 'bottom',
    });

    await toast.present();
  }




}
