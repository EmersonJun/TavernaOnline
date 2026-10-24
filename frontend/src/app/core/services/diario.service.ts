import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Anotacao, AnotacaoRequest } from '../models/diario.model';

@Injectable({ providedIn: 'root' })
export class DiarioService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  listar(campanhaId: string): Observable<Anotacao[]> {
    return this.http.get<Anotacao[]>(`${this.api}/campanhas/${campanhaId}/anotacoes`);
  }

  criar(campanhaId: string, request: AnotacaoRequest): Observable<Anotacao> {
    return this.http.post<Anotacao>(`${this.api}/campanhas/${campanhaId}/anotacoes`, request);
  }

  excluir(anotacaoId: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/anotacoes/${anotacaoId}`);
  }
}
