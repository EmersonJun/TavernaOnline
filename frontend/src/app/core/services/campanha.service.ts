import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Campanha, CriarCampanhaRequest, Participante } from '../models/campanha.model';

@Injectable({ providedIn: 'root' })
export class CampanhaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/campanhas`;

  listarMinhas(): Observable<Campanha[]> {
    return this.http.get<Campanha[]>(this.url);
  }

  criar(request: CriarCampanhaRequest): Observable<Campanha> {
    return this.http.post<Campanha>(this.url, request);
  }

  entrarPorCodigo(codigoConvite: string): Observable<Campanha> {
    return this.http.post<Campanha>(`${this.url}/entrar`, { codigoConvite });
  }

  listarParticipantes(campanhaId: string): Observable<Participante[]> {
    return this.http.get<Participante[]>(`${this.url}/${campanhaId}/participantes`);
  }
}
