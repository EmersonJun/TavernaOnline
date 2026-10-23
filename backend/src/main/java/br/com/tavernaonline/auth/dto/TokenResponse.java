package br.com.tavernaonline.auth.dto;

public record TokenResponse(
        String token,
        String tipo,
        long expiraEmMinutos
) {
    public static TokenResponse bearer(String token, long expiraEmMinutos) {
        return new TokenResponse(token, "Bearer", expiraEmMinutos);
    }
}
