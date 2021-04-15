package com.miniproject.moviebook.auth;


import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(PrincipalDetails::new).orElseThrow(() -> new UsernameNotFoundException("데이터베이스에서 찾을 수 없습니다.")); // 입력받은 username에 해당하는 사용자가 있다면, PrincipalDetails 객체를 생성한다
    }
}
