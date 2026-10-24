package br.com.tavernaonline.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final AutenticacaoUserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(JwtService jwtService, AutenticacaoUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = extrairToken(accessor);
            if (token == null || !jwtService.tokenValido(token)) {
                throw new IllegalArgumentException("Token JWT ausente ou invalido na conexao WebSocket");
            }
            String email = jwtService.extrairSubject(token);
            UserDetails usuario = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                    usuario, null, usuario.getAuthorities());
            accessor.setUser(autenticacao);
        }

        return message;
    }

    private String extrairToken(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith(PREFIXO)) {
            return header.substring(PREFIXO.length());
        }
        return null;
    }
}
