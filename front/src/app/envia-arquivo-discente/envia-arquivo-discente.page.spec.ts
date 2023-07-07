import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EnviaArquivoDiscentePage } from './envia-arquivo-discente.page';

describe('EnviaArquivoDiscentePage', () => {
  let component: EnviaArquivoDiscentePage;
  let fixture: ComponentFixture<EnviaArquivoDiscentePage>;

  beforeEach(async(() => {
    fixture = TestBed.createComponent(EnviaArquivoDiscentePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
