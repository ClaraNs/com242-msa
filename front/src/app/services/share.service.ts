import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ShareService {
  apiUrl = 'http://localhost:8081'; // Substitua pela sua URL

  constructor(private http: HttpClient) { }

  getAlunoPorMatricula(matricula: string) {
    const url = `${this.apiUrl}/alunos/${matricula}`;
    return this.http.get(url);
  }

  getProfPorMatricula(matricula: string) {
    const url = `${this.apiUrl}/profs/${matricula}`;
    return this.http.get(url);
  }

  uploadArtigo(file: File, fields: any) {
    const url = `${this.apiUrl}/artigoupload`;
  
    const formData = new FormData();
    formData.append('pdfFile', file, file.name);
  
    // Adiciona os campos adicionais ao FormData
    Object.keys(fields).forEach((key) => {
      formData.append(key, fields[key]);
    });
  
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
  
    return this.http.post(url, formData, { headers });
  }

  retornaArtigosPorMatricula(matricula: string) {
    const url = `${this.apiUrl}/aluno/${matricula}/artigos`; 
    return this.http.get(url);
  }

  retornaArtigos() {
    const url = `${this.apiUrl}/artigo`; 
    return this.http.get(url);
  }

  reenviarArtigo(file: File, idArtigo: number) {
    const url = `${this.apiUrl}/artigo/${idArtigo}/reenviar`;
  
    const formData = new FormData();
    formData.append('pdfFile', file, file.name);
  
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
  
    return this.http.post(url, formData, { headers });
  }

  retornaArtigosPorMatriculaProf(matricula: string) {
    const url = `${this.apiUrl}/orientador/${matricula}/artigos`; 
    return this.http.get(url);
  }

  avaliarArtigoOrientador(idArtigo: number, consideracoes: string, correcao: boolean) {
    const url = `${this.apiUrl}/artigo/${idArtigo}/avaliacao`;
    const params = new HttpParams()
      .set('consideracoes', consideracoes)
      .set('correcao', correcao ? 'true' : 'false');
  
    return this.http.post(url, null, { params: params,  responseType: 'text' });
  }

  retornaBancasMatricula(matricula : string){
    const url = `${this.apiUrl}/professor/${matricula}/bancas`; 
    return this.http.get(url);
  }

  retornaBancas(){
    const url = `${this.apiUrl}/banca`; 
    return this.http.get(url);
  }

  retornaBancasPendentes(){
    const url = `${this.apiUrl}/banca/aguardandoaprovacao`; 
    return this.http.get(url);
  }

  retornaBancasSemDataMatricula(matricula : string){
    const url = `${this.apiUrl}/banca/aguardandonovasdatas/${matricula}`; 
    return this.http.get(url);
  }

  autorizarBanca(idBanca:number, aprovacao:boolean){
    const url = `${this.apiUrl}/banca/aguardandoaprovacao/${idBanca}`;
    const params = new HttpParams()
      .set('aprovacao', aprovacao);
  
    return this.http.post(url, null, { params: params,  responseType: 'text' });
  }

  solicitarBanca(idArtigo: number) {
    const url = `${this.apiUrl}/artigo/${idArtigo}/banca/cadastro`;
  
    return this.http.post(url, null, { responseType: 'text' });
  }

  cadastrarProfBanca(idBanca:number, matricula:string){
    const url = `${this.apiUrl}/composicao/${idBanca}/cadastrar`;
    const params = new HttpParams()
      .set('matricula', matricula);
  
    return this.http.post(url, null, { params: params,  responseType: 'text' });
  }

  marcarDataBanca(idBanca:number, data:string, horaInicio:string){
    const url = `${this.apiUrl}/banca/${idBanca}/cadastra/avaliacao`;
    const params = new HttpParams()
      .set('data', data)
      .set('horaInicio', horaInicio);
  
    return this.http.post(url, null, { params: params,  responseType: 'text' });
  }

  avaliarArtigoBanca(idBanca: number, matricula:string, nota:number, consideracoes: string, correcao: boolean) {
    const url = `${this.apiUrl}/professor/${matricula}/banca/${idBanca}/cadastrar/avaliacao`;
    const params = new HttpParams()
      .set('consideracao', consideracoes)
      .set('nota', nota)
      .set('correcao', correcao ? 'true' : 'false');
  
    return this.http.post(url, null, { params: params,  responseType: 'text' });
  }
}