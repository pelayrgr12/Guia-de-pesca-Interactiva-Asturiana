import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GestionPage } from './gestion.page';

describe('GestionPage', () => {
  let component: GestionPage;
  let fixture: ComponentFixture<GestionPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(GestionPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
