# 미니 프로젝트 (MovieBook)

## 📆 개발 기간
- 2021년 04월 09일 ~ 2021년 04월 22일

## ✨ 멤버 구성
- 프론트엔드 (React): 정찬엽, 정성묵
  - 깃허브 주소: https://github.com/rayrayj92/movie-book
- 백엔드 (Spring): 채진욱, 엄민식

## 📽 시연 영상
- https://youtu.be/c6mZIPFTwj4

## 💻 백엔드 개발 환경
- Java: `jdk 1.11.0`
- Framework: `SpringBoot`
- Server: `Amazon EC2 Ubuntu`
- Database: `Amazon RDS Mysql`
- Web Scrapping: `Python3 Selenium`

## 💻 백엔드 주요 기능

### 회원 가입
- 회원 가입 요청
```
POST
/api/signup
```
- 유저 아이디 중복 체크
```
POST
/api/signup/{username}
```

### 로그인
- 로그인
```
POST
/api/login
```

### 인증
- JWT: `AccessToken`과 `RefreshToken`을 통한 인증을 구현했습니다.

### 영화 정보 관련
- 무작위 7개 영화 목록 조회
```
GET
/api/movies/random
```
- 영화 상세 정보 조회
```
GET
/api/movies/details/{m_id}
```
- 영화 검색 (부분 단어로 검색가능) 목록 조회 (페이징 처리)
	- Spring Data JPA의 `Slice`을 사용했습니다.
```
GET
/api/movies?search=어벤져스&page=1
```

### 리뷰 관련
- 해당 영화 리뷰 목록 조회 (페이징 처리)
	- Spring Data JPA의 `Page`을 사용했습니다.
```
GET
/api/reviews/list/{m_id}?page=1
```
- 해당 영화 리뷰 작성
```
POST
/api/reviews/authentication/{m_id}
```
- 해당 영화 리뷰 수정
```
PUT
/api/reviews/authentication/{r_id}
```
- 해당 영화 리뷰 삭제
```
DELETE
/api/reviews/authentication/{r_id}
```

### 컬렉션 관련
- 해당 유저의 영화 컬렉션 조회
```
GET
/api/collections/list/{u_id}
```
- 해당 유저의 영화 컬렉션에 영화 추가
```
POST
/api/collections/authentication/{m_id}
```
- 해당 유저의 영화 컬렉션에서 영화 삭제
```
DELETE
/api/collections/authentication/{c_id}
```
