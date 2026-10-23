package br.com.tavernaonline.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final AutenticacaoUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, AutenticacaoUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extrairToken(request);

        if (token != null && jwtService.tokenValido(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtService.extrairSubject(token);
            UserDetails usuario = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                    usuario, null, usuario.getAuthorities());
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }

        filterChain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith(PREFIXO)) {
            return header.substring(PREFIXO.length());
        }
        return null;
    }
}
