import { Component, OnInit, ViewChild } from '@angular/core';
import { IonModal, ToastController } from '@ionic/angular';
import { ShareService } from '../services/share.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-dash-docente',
  templateUrl: './dash-docente.page.html',
  styleUrls: ['./dash-docente.page.scss', '../../global.css'],
})
export class DashDocentePage implements OnInit {

  @ViewChild(IonModal) modal: IonModal | undefined;
  artigoAtual: any;
  artigos:any = [];
  loading:any = true;
  isModalOpen: boolean = false;
  isModalOpenNota: boolean = false;
  isModalOpenData: boolean = false;
  isModalOpenInfo: boolean = false;
  isModalOpenInfoBanca: boolean = false;
  matricula: any;
  consideracoes:any;
  correcao:boolean = false;
  prof:any;
  bancas:any = [];
  loadingBanca:any = true;
  bancaAtual:any;
  hora:any;
  data:any;
  pipe: DatePipe = new DatePipe('en-US');
  consideracoesNota: any;
  nota:any;
  correcaoNota: boolean = false;


  constructor(private route: ActivatedRoute, private router: Router, private share: ShareService, private toastCtrl: ToastController) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (this.router.getCurrentNavigation()?.extras.state != null) {
        let dados: any = this.router.getCurrentNavigation()?.extras?.state;
        this.prof = dados;
        this.matricula = dados.matricula;
        this.carregaArtigos();
        this.carregaBancas();
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
    this.isModalOpenNota = false;

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

  abrirModalData(isOpen: boolean) {
    this.isModalOpenData = isOpen;
  }

  abrirModalNota(isOpen: boolean) {
    this.isModalOpenNota = isOpen;
  }

  onWillDismiss(event:any) {
    this.isModalOpen = false;
    this.carregaArtigos();
  }

  onWillDismissInfo(event:any) {
    this.isModalOpenInfo = false;
  }

  onWillDismissData(event:any) {
    this.isModalOpenData = false;
    this.carregaBancas();
  }

  onWillDismissNota(event:any) {
    this.isModalOpenData = false;
    this.carregaBancas();
  }

  onWillDismissInfoBanca(event:any) {
    this.isModalOpenInfoBanca = false;
    this.carregaBancas();
  }

  carregaArtigos(){
    this.loading = true;
    this.share.retornaArtigosPorMatriculaProf(this.matricula).subscribe((data:any)=>{
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
    this.share.retornaBancasMatricula(this.matricula).subscribe((data:any)=>{
      this.bancas = [];
      data.forEach((x:any)=>{
        let banca = {
          idbanca: x.idBanca,
          dataRegistro: x.dataRegistro,
          dataAtualizacao: x.dataAtualizacao,
          dataAvaliacao: x.dataAvaliacao,
          artigoAvaliado: x.artigoAvaliado,
          status: x.status,
          correcao: x.correcao
        }

        this.bancas.push(banca);
      });
      this.loadingBanca = false;
    });
  }

  solicitarBanca(art:any){
    this.share.solicitarBanca(art.idartigo).subscribe((data:any)=>{
      if(data >= 0){
        this.presentToast("Banca cadastrada com sucesso.");
        this.addProfNaBanca(data);
      }else{
        this.presentToast("Algo deu errado.");
      }
    });
  }

  addProfNaBanca(idbanca: number){
    this.share.cadastrarProfBanca(idbanca, this.matricula).subscribe((data:any)=>{
      this.presentToast(data);
      this.carregaBancas();
    });
  }

  insereNota(){
    this.share.avaliarArtigoBanca(this.bancaAtual.idbanca, this.matricula, this.nota, 
      this.consideracoesNota, this.correcaoNota).subscribe((data:any)=>{
      this.presentToast(data);
      this.fechar();
      this.carregaBancas();
    });
  }

  avaliar(art: any){
    this.artigoAtual = art;
    this.consideracoes = null;
    this.correcao = false;
    this.abrirModal(true);
  }

  abrirInfo(art: any){
    this.artigoAtual = art;
    this.abrirModalInfo(true);
  }

  infoBanca(banca:any){
    this.bancaAtual = banca;
    this.abrirModalInfoBanca(true);
  }

  definirData(banca:any){
    this.bancaAtual = banca;
    this.abrirModalData(true);
  }

  abrirNota(banca:any){
    this.bancaAtual = banca;
    this.abrirModalNota(true);
  }

  selecionarData(){
    let datac:any = this.pipe.transform(this.data, 'yyyy-MM-dd')
    this.share.marcarDataBanca(this.bancaAtual.idbanca, datac, this.hora).subscribe((data:any)=>{
      this.presentToast(data);
      this.data = null;
      this.hora = null;
      this.carregaBancas();
      this.fechar();
    });
  }

  enviarAvaliacao(){
    console.log(this.correcao);
    this.share.avaliarArtigoOrientador(this.artigoAtual.idartigo, this.consideracoes, this.correcao)
    .subscribe((data:any)=>{
      this.presentToast(data);
      this.fechar();
    });
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
