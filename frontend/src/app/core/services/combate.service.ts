import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Combate } from '../models/sessao.model';

export interface CombatenteEntrada {
  nome: string;
  iniciativa: number;
  personagemId: string | null;
}

@Injectable({ providedIn: 'root' })
export class CombateService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  estado(sessaoId: string): Observable<Combate> {
    return this.http.get<Combate>(`${this.api}/sessoes/${sessaoId}/combate`);
  }

  iniciar(sessaoId: string, combatentes: CombatenteEntrada[]): Observable<Combate> {
    return this.http.post<Combate>(`${this.api}/sessoes/${sessaoId}/combate/iniciar`, { combatentes });
  }

  proximoTurno(sessaoId: string): Observable<Combate> {
    return this.http.post<Combate>(`${this.api}/sessoes/${sessaoId}/combate/proximo-turno`, {});
  }

  encerrar(sessaoId: string): Observable<Combate> {
    return this.http.post<Combate>(`${this.api}/sessoes/${sessaoId}/combate/encerrar`, {});
  }
}
