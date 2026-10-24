export interface Atributos {
  forca: number;
  destreza: number;
  constituicao: number;
  inteligencia: number;
  sabedoria: number;
  carisma: number;
}

export interface Pericia {
  nome: string;
  bonus: number;
  proficiente: boolean;
}

export interface ItemInventario {
  nome: string;
  quantidade: number;
  peso: number;
  descricao: string | null;
}

export interface Personagem {
  id: string;
  campanhaId: string;
  donoId: string;
  nome: string;
  classe: string | null;
  raca: string | null;
  nivel: number;
  pvAtual: number;
  pvMaximo: number;
  classeArmadura: number;
  atributos: Atributos;
  pericias: Pericia[];
  inventario: ItemInventario[];
  versao: number;
}

export type PersonagemRequest = Omit<Personagem, 'id' | 'campanhaId' | 'donoId' | 'versao'>;
