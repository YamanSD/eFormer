package eformer.back.eformer_backend.utility.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jManager;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jManager,
                                   UserDetailsService userDetailsService) {
        this.jManager = jManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        /* Contains the token */
        final String authHeader = request.getHeader("Authorization");

        /* A valid token must start with `Bearer` */
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            /* Invalid token */
            filterChain.doFilter(request, response);
            return;
        }

        /* Skip `Bearer ` */
        final String token = authHeader.split(" ")[1];
        final String username = jManager.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = this.userDetailsService.loadUserByUsername(username);

            if (jManager.isTokenValid(token, user)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

                /* Enforce authToken using request details */
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                /* Update context holder */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        }
    }
}
