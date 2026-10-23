package br.com.tavernaonline.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> tratarRegraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResposta> tratarCredenciaisInvalidas(BadCredentialsException ex, HttpServletRequest req) {
        return construir(HttpStatus.UNAUTHORIZED, "Credenciais invalidas", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErroResposta.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapearCampo)
                .toList();
        ErroResposta corpo = new ErroResposta(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Dados invalidos",
                req.getRequestURI(),
                campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    private ErroResposta.CampoInvalido mapearCampo(FieldError erro) {
        return new ErroResposta.CampoInvalido(erro.getField(), erro.getDefaultMessage());
    }

    private ResponseEntity<ErroResposta> construir(HttpStatus status, String mensagem, HttpServletRequest req) {
        ErroResposta corpo = ErroResposta.de(status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI());
        return ResponseEntity.status(status).body(corpo);
    }
}
