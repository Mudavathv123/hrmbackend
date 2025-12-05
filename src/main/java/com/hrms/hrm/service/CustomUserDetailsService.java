package com.hrms.hrm.service;

import com.hrms.hrm.model.User;
import com.hrms.hrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UsernameNotFoundException("User inactive");
        }

        Collection<? extends GrantedAuthority> authorities = List
                .of(new SimpleGrantedAuthority(user.getRole().name()));

        boolean accountNonLocked = user.getLockedUntil() == null || user.getLockedUntil().isBefore(LocalDateTime.now());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                accountNonLocked,
                authorities
        );
    }
}
