export type Papel = 'MESTRE' | 'JOGADOR';

export interface Campanha {
  id: string;
  nome: string;
  descricao: string | null;
  sistema: string;
  codigoConvite: string;
  ativa: boolean;
  meuPapel: Papel;
}

export interface CriarCampanhaRequest {
  nome: string;
  descricao: string | null;
  sistema: string;
}

export interface Participante {
  usuarioId: string;
  nome: string;
  email: string;
  papel: Papel;
}
