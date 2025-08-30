import { TestBed } from '@angular/core/testing';

import { HistorialserviceService } from './historialservice.service';

describe('HistorialserviceService', () => {
  let service: HistorialserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HistorialserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
