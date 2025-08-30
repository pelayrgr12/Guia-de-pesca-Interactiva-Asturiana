import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TablaMedidasPage } from './tabla-medidas.page';

describe('TablaMedidasPage', () => {
  let component: TablaMedidasPage;
  let fixture: ComponentFixture<TablaMedidasPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TablaMedidasPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
