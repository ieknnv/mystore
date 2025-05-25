package org.ieknnv.mystore.service;

import java.util.NoSuchElementException;

import org.ieknnv.mystore.entity.User;
import org.ieknnv.mystore.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found"));
    }
}
