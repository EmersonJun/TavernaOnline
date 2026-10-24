export type Visibilidade = 'PUBLICA' | 'SOMENTE_MESTRE';

export interface Anotacao {
  id: string;
  campanhaId: string;
  sessaoId: string | null;
  autorId: string;
  autorNome: string;
  titulo: string;
  conteudo: string;
  visibilidade: Visibilidade;
  criadoEm: string;
}

export interface AnotacaoRequest {
  titulo: string;
  conteudo: string;
  visibilidade: Visibilidade;
  sessaoId: string | null;
}
