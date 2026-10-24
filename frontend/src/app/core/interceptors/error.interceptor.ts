import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((erro: HttpErrorResponse) => {
      if (erro.status === 401) {
        auth.sair();
      }
      snackBar.open(extrairMensagem(erro), 'Fechar', { duration: 5000 });
      return throwError(() => erro);
    })
  );
};

function extrairMensagem(erro: HttpErrorResponse): string {
  if (erro.error && typeof erro.error === 'object' && 'mensagem' in erro.error) {
    return String((erro.error as { mensagem: string }).mensagem);
  }
  if (erro.status === 0) {
    return 'Nao foi possivel conectar ao servidor';
  }
  return 'Ocorreu um erro inesperado';
}
