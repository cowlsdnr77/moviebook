package com.miniproject.moviebook.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.moviebook.auth.PrincipalDetails;
import com.miniproject.moviebook.dto.UserInfoDto;
import com.miniproject.moviebook.model.RefreshToken;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.Optional;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("로그인 시도중");


        try{

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println(user);


            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());


            Authentication authentication = authenticationManager.authenticate(authenticationToken);


            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            System.out.println(principalDetails.getUser().getUsername());


            return authentication;
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이 완료되었다는 것");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

//        기존 jwt 토큰 생성 방식
//        String jwtAccessToken = JWT.create()
//                .withSubject("cos 토큰")
//                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
//                .withClaim("id", principalDetails.getUser().getU_id())
//                .withClaim("username", principalDetails.getUser().getUsername())
//                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
//
//        String jwtRefreshToken = JWT.create()
//                .withSubject("refresh 토큰")
//                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME * 48)) //유효기간: 하루
//                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        byte[] keyBytes = Decoders.BASE64.decode(JwtProperties.SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String jwtAccessToken = Jwts.builder()
                .setSubject("cos 토큰")
                .claim("u_id", principalDetails.getUser().getU_id())
                .claim("username", principalDetails.getUser().getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .compact();

        String jwtRefreshToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME * 48)) //유효시간: 하루
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        ObjectMapper objectMapper = new ObjectMapper();


        UserInfoDto userInfoDto = new UserInfoDto(principalDetails.getUser().getU_id(),principalDetails.getUser().getName());
        String userInfoJson = objectMapper.writeValueAsString(userInfoDto);

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtAccessToken); //헤더에 Authorization으로 담김
        response.addHeader("Refresh-Token", JwtProperties.TOKEN_PREFIX + jwtRefreshToken); //헤더에 Refresh-Token 추가
        //토큰 정보 body에 넣을떄
        response.addHeader("Content-type","application/json");
        response.getWriter().write(userInfoJson);

        // refreshToken 데이터베이스에 해당 유저의 기존 refreshToken이 남아있다면 삭제
        Optional<RefreshToken> remainRefreshToken = refreshTokenRepository.findById(principalDetails.getUser().getU_id());
        if(remainRefreshToken.isPresent()){
            refreshTokenRepository.deleteById(principalDetails.getUser().getU_id());
        }


        // refreshToken db에 저장
        RefreshToken refreshToken = new RefreshToken(principalDetails.getUser().getU_id(), jwtRefreshToken);
        refreshTokenRepository.save(refreshToken);
    }
}
