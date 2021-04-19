package com.miniproject.moviebook.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.moviebook.auth.PrincipalDetails;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader: " + jwtHeader);

        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }


        String jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).acceptLeeway(1).acceptExpiresAt(5).build();
            DecodedJWT jwt = verifier.verify(jwtToken);
            String username = jwt.getClaim("username").asString();
            if (username != null) {
                System.out.println("서명 검증이 정상적으로 됨");
                Optional<User> userEntity = userRepository.findByUsername(username);

                PrincipalDetails principalDetails = userEntity.map(PrincipalDetails::new).orElse(null); // 입력받은 username에 해당하는 사용자가 있다면, PrincipalDetails 객체를 생성한다.

                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            }
        } catch(TokenExpiredException e) {
            Map<String, String> map = new HashMap<>();
            map.put("errMsg", "로그인 시간이 만료되었습니다.");
            String mapJson = objectMapper.writeValueAsString(map);
            response.getWriter().write(mapJson);
        } catch(JWTVerificationException e) {
            Map<String, String> map = new HashMap<>();
            map.put("errMsg", "유효한 토큰이 아닙니다.");
            String mapJson = objectMapper.writeValueAsString(map);
            response.getWriter().write(mapJson);
        }
    }
}
