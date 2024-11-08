//package com.ferozfaiz.security.filter;
//
//
//import com.ferozfaiz.security.jwt.util.JwtUtil;
//import io.micrometer.common.lang.NonNull;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.ferozfaiz.security.user.UserService;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import java.io.IOException;
//
//@Component
//public class JwtRequestFilter extends OncePerRequestFilter {
//    @Autowired
//    private UserService userDetailsService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
////    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
////            throws ServletException, IOException {
////        final String authorizationHeader = request.getHeader("Authorization");
////
////        String username = null;
////        String jwt = null;
////
////        // Check if the Authorization header contains a Bearer token
////        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
////            jwt = authorizationHeader.substring(7); // Remove "Bearer " prefix
////            username = jwtUtil.getUsernameFromToken(jwt);
////        }
////
////        // Validate the token
////        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
////            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
////
////            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
////                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
////                        userDetails, null, userDetails.getAuthorities());
////
////                authenticationToken
////                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////
////                // Set the authentication in the context
////                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
////            }
////        }
////
////        chain.doFilter(request, response);
////    }
//
//    public JwtRequestFilter(HandlerExceptionResolver handlerExceptionResolver, JwtUtil jwtUtil, UserService userDetailsService) {
//        this.handlerExceptionResolver = handlerExceptionResolver;
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }
//
//    private final HandlerExceptionResolver handlerExceptionResolver;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            final String jwt = authHeader.substring(7);
//            final String userEmail = jwtUtil.getUsernameFromToken(jwt);
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (userEmail != null && authentication == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//
//                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//
//            filterChain.doFilter(request, response);
//        } catch (Exception exception) {
//            handlerExceptionResolver.resolveException(request, response, null, exception);
//        }
//    }
//}
