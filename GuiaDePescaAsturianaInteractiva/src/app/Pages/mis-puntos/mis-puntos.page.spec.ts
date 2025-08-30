import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MisPuntosPage } from './mis-puntos.page';

describe('MisPuntosPage', () => {
  let component: MisPuntosPage;
  let fixture: ComponentFixture<MisPuntosPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(MisPuntosPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
