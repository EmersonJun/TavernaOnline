import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    try {
      localStorage.clear();
    } catch {
    }
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('deve iniciar nao autenticado', () => {
    expect(service.estaAutenticado()).toBeFalse();
  });

  it('deve guardar o token e marcar como autenticado apos login', () => {
    service.login({ email: 'mestre@taverna.com', senha: 'senha1234' }).subscribe();

    const req = http.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'token-jwt', tipo: 'Bearer', expiraEmMinutos: 120 });

    expect(service.estaAutenticado()).toBeTrue();
    expect(service.obterToken()).toBe('token-jwt');
  });
});
