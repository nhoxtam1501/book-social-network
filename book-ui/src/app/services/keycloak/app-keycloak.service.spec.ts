import { TestBed } from '@angular/core/testing';

import { AppKeycloakService } from './app-keycloak.service';

describe('AppKeycloakService', () => {
  let service: AppKeycloakService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppKeycloakService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
