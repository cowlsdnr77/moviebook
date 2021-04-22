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

//스프링 시큐리티에 UsernamePasswordAuthenticationFilter 가 있음
//******** /login 요청해서 username,password를 POST로 전송하면
// UsernamePasswordAuthenticationFilter 필터가 동작함 -> 근데 SecurityConfig에서 formLogin을 안쓰게해서 동작안함
// 필터가 동작하려면 UsernamePasswordAuthenticationFilter를 extends한 JwtAuthenticationFilter를 SecurityConfig에 등록해아함

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("로그인 시도중");


        try{

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println(user);


            // 로그인 시도를 위해 토큰을 만든다.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());


            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨
            // DB에 있는 username과 password 가 일치한다는 뜻
            // Authentication에는 내가 로그인한 정보가 담김
            Authentication authentication = authenticationManager.authenticate(authenticationToken);


            //=> 로그인이 되었다는 뜻
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            System.out.println(principalDetails.getUser().getUsername());


            return authentication;
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return null;
    }


    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
    // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이 완료되었다는 것");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        byte[] keyBytes = Decoders.BASE64.decode(JwtProperties.SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String jwtAccessToken = Jwts.builder()
                .setSubject("cos 토큰")
                .claim("u_id", principalDetails.getUser().getU_id())
                .claim("username", principalDetails.getUser().getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME)) //AccessToken 유효시간: 30분
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .compact();

        String jwtRefreshToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME * 48)) //RefreshToken 유효시간: 24시간
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        ObjectMapper objectMapper = new ObjectMapper();


        UserInfoDto userInfoDto = new UserInfoDto(principalDetails.getUser().getU_id(),principalDetails.getUser().getName());
        String userInfoJson = objectMapper.writeValueAsString(userInfoDto);

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtAccessToken); //헤더에 Access-Token 추가
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
