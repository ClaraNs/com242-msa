import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashDocentePage } from './dash-docente.page';

describe('DashDocentePage', () => {
  let component: DashDocentePage;
  let fixture: ComponentFixture<DashDocentePage>;

  beforeEach(async(() => {
    fixture = TestBed.createComponent(DashDocentePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
