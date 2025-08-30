import { TestBed } from '@angular/core/testing';

import { MedidaService } from './medida.service';

describe('MedidaService', () => {
  let service: MedidaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MedidaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
