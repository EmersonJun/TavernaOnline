import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Personagem, PersonagemRequest } from '../models/personagem.model';

@Injectable({ providedIn: 'root' })
export class PersonagemService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  listar(campanhaId: string): Observable<Personagem[]> {
    return this.http.get<Personagem[]>(`${this.api}/campanhas/${campanhaId}/personagens`);
  }

  criar(campanhaId: string, request: PersonagemRequest): Observable<Personagem> {
    return this.http.post<Personagem>(`${this.api}/campanhas/${campanhaId}/personagens`, request);
  }

  atualizar(personagemId: string, request: PersonagemRequest): Observable<Personagem> {
    return this.http.put<Personagem>(`${this.api}/personagens/${personagemId}`, request);
  }

  excluir(personagemId: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/personagens/${personagemId}`);
  }
}
