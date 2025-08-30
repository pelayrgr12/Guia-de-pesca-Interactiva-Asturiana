import { TestBed } from '@angular/core/testing';

import { PuntoService } from './punto.service';

describe('PuntoService', () => {
  let service: PuntoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PuntoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
