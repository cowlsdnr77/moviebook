package com.miniproject.moviebook.jwt;

public interface JwtProperties {
    String SECRET = "cos";
    String SECRET_KEY = "c3ByaW5nLWJvb3Qtc2VjdXJpdHktand0LW1pbmlwcm9qZWN0LWppbm9vay1zcHJpbmctYm9vdC1zZWN1cml0eS1qd3QtbWluaXByb2plY3QK";
    int EXPIRATION_TIME = 60000*30;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Access-Token";
}
