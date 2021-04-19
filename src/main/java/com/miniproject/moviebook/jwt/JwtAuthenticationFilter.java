package com.miniproject.moviebook.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.moviebook.auth.PrincipalDetails;
import com.miniproject.moviebook.dto.UserInfoDto;
import com.miniproject.moviebook.model.User;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;


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


        String jwtToken = JWT.create()
                .withSubject("cos 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getU_id())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        ObjectMapper objectMapper = new ObjectMapper();


        UserInfoDto userInfoDto = new UserInfoDto(principalDetails.getUser().getU_id(),principalDetails.getUser().getName());
        String userInfoJson = objectMapper.writeValueAsString(userInfoDto);

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken); //헤더에 Authorization으로 담김

        //토큰 정보 body에 넣을떄
        response.addHeader("Content-type","application/json");
        response.getWriter().write(userInfoJson);

    }
}
