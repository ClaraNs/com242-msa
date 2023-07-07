import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashCoordenadorPage } from './dash-coordenador.page';

describe('DashCoordenadorPage', () => {
  let component: DashCoordenadorPage;
  let fixture: ComponentFixture<DashCoordenadorPage>;

  beforeEach(async(() => {
    fixture = TestBed.createComponent(DashCoordenadorPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
