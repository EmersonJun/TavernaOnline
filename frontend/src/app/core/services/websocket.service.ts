import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private client: Client | null = null;
  private readonly pendentes = new Set<() => void>();

  conectar(token: string): void {
    if (this.client) {
      return;
    }
    this.client = new Client({
      brokerURL: environment.wsUrl,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => this.pendentes.forEach((inscrever) => inscrever())
    });
    this.client.activate();
  }

  observar<T>(destino: string): Observable<T> {
    return new Observable<T>((subscriber) => {
      let assinatura: StompSubscription | null = null;
      const inscrever = () => {
        if (this.client && this.client.connected && !assinatura) {
          assinatura = this.client.subscribe(destino, (mensagem: IMessage) => {
            subscriber.next(JSON.parse(mensagem.body) as T);
          });
        }
      };
      this.pendentes.add(inscrever);
      inscrever();
      return () => {
        this.pendentes.delete(inscrever);
        assinatura?.unsubscribe();
      };
    });
  }

  enviar(destino: string, corpo: unknown): void {
    this.client?.publish({ destination: destino, body: JSON.stringify(corpo) });
  }

  desconectar(): void {
    this.pendentes.clear();
    this.client?.deactivate();
    this.client = null;
  }
}
