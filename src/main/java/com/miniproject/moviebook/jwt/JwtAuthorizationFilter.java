package com.miniproject.moviebook.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.moviebook.auth.PrincipalDetails;
import com.miniproject.moviebook.model.RefreshToken;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.RefreshTokenRepository;
import com.miniproject.moviebook.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
import java.security.Key;
import java.util.*;


// ******* 인증/권한 이 필요한 경우 타는 filter
// 시큐리티가 filter를 가지고 있는데 그 필터 중 BasicAuthenticationFilter라는 것이 있음
// *******권한이나 인증이 필요한 특정 주소를 요청했을때 위 필터를 무조건 타게 되어있음 ********
// 만약 권한이나 인증이 필요한 주소가 아니라면 위 필터를 거치지 않음
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    byte[] keyBytes = Decoders.BASE64.decode(JwtProperties.SECRET_KEY);
    Key key = Keys.hmacShaKeyFor(keyBytes);

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //인증이나 권한이 필요한 주소요청이 있을때 해당 필터를 타게됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

        String jwtHeader = request.getHeader("Access-Token"); // Request 요청의 헤더에 있는 Access-Token 확인
        System.out.println("jwtHeader: " + jwtHeader);

        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }


        String jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");

        ObjectMapper objectMapper = new ObjectMapper();

        // RefreshToken이 들어왔다는 것은 AccessToken이 만료됐다는 것
        String refreshjwtHeader = request.getHeader("Refresh-Token");

        if(refreshjwtHeader != null && refreshjwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) { //accessToken과 refreshToken이 둘다 있다면
            String refreshToken = refreshjwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");
            try{
                // 1. refreshToken의 유효성을 검증한다.
                if (!validateToken(refreshToken).equals("succeeded token")) {
                    throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
                }
                // 2. 만료된 accessToken의 claim에서 user 정보(u_id)를 꺼내온다.
                Long u_id = ((Number) parseClaims(jwtToken).get("u_id")).longValue();
                String username = (String) parseClaims(jwtToken).get("username");
                // 3-1. db에 저장되어있는 refreshToken 의 value(토큰 값)을 가져온다.
                RefreshToken refreshTokenfromDb = refreshTokenRepository.findById(u_id).orElseThrow(
                        () -> new RuntimeException("로그아웃 된 사용자입니다.")
                );
                // 3-2. 꺼낸 user 정보와 db에 저장되어있는 refreshToken의 key(u_id) 값을 비교한다.
                if(!refreshTokenfromDb.getValue().equals(refreshToken)) {
                    throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
                }
                // 4. 같다면 새로운 accessToken과 refreshToken을 만들어서 클라이언트에게 준다.
                String jwtAccessToken = Jwts.builder()
                        .setSubject("cos 토큰")
                        .claim("u_id", u_id)
                        .claim("username", username)
                        .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact();

                String jwtRefreshToken = Jwts.builder()
                        .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME * 48))
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact();
                // 5. 새로 만든 refreshToken을 db에 update한다.
                RefreshToken newRefreshToken = refreshTokenfromDb.updateValue(jwtRefreshToken);
                refreshTokenRepository.save(newRefreshToken);

                // 6. 새로운 토큰(AccessToken + RefreshToken) 발급
                response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtAccessToken); //헤더에 Authorization으로 담김
                response.addHeader("Refresh-Token", JwtProperties.TOKEN_PREFIX + jwtRefreshToken); //헤더에 Refresh-Token 추가

                // 7. 토큰 재발급 메시지를 응답으로 보냄
                Map<String, String> map = new HashMap<>();
                map.put("msg", "토큰 재발급이 완료되었습니다.");
                String mapJson = objectMapper.writeValueAsString(map);
                response.getWriter().write(mapJson);

            } catch(RuntimeException e) {
                Map<String, String> map = new HashMap<>();
                map.put("msg", "재로그인이 필요합니다.");
                String mapJson = objectMapper.writeValueAsString(map);
                response.getWriter().write(mapJson);
            }

        } else { //클라이언트에서 accessToken만 보낼때 유효성 검증
            if (validateToken(jwtToken).equals("succeeded token")) {
                System.out.println("서명 검증이 정상적으로 됨");
                String username = (String) parseClaims(jwtToken).get("username");
                Optional<User> userEntity = userRepository.findByUsername(username);

                PrincipalDetails principalDetails = userEntity.map(PrincipalDetails::new).orElse(null); // 입력받은 username에 해당하는 사용자가 있다면, PrincipalDetails 객체를 생성한다.

                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            } else if(validateToken(jwtToken).equals("expired token")){
                Map<String, String> map = new HashMap<>();
                map.put("msg", "로그인 시간이 만료되었습니다.");
                String mapJson = objectMapper.writeValueAsString(map);
                response.getWriter().write(mapJson);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("msg", "유효하지 않은 JWT 토큰입니다.");
                String mapJson = objectMapper.writeValueAsString(map);
                response.getWriter().write(mapJson);
            }
        }
    }

    public String validateToken(String token) { //토큰 검증
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return "succeeded token";
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
            return "expired token";
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return "invalid token";
    }

    private Claims parseClaims(String accessToken) { //토큰의 claim 접근
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
