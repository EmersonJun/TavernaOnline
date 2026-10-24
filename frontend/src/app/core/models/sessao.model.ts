export type StatusSessao = 'AGENDADA' | 'EM_ANDAMENTO' | 'ENCERRADA';

export interface Sessao {
  id: string;
  campanhaId: string;
  numero: number;
  titulo: string;
  status: StatusSessao;
  iniciadaEm: string | null;
  encerradaEm: string | null;
  combateAtivo: boolean;
  rodadaAtual: number;
}

export type TipoMensagem = 'TEXTO' | 'SISTEMA';

export interface Mensagem {
  id: string;
  sessaoId: string;
  autorId: string | null;
  autorNome: string | null;
  tipo: TipoMensagem;
  conteudo: string;
  criadoEm: string;
}

export interface Rolagem {
  id: string;
  sessaoId: string;
  autorId: string;
  autorNome: string;
  personagemId: string | null;
  notacao: string;
  resultados: number[];
  modificador: number;
  total: number;
  motivo: string | null;
  secreta: boolean;
  criadoEm: string;
}

export interface Combatente {
  id: string;
  nome: string;
  iniciativa: number;
  ordem: number;
  personagemId: string | null;
}

export interface Combate {
  sessaoId: string;
  combateAtivo: boolean;
  rodadaAtual: number;
  indiceTurnoAtual: number;
  participanteDaVezId: string | null;
  ordem: Combatente[];
}
