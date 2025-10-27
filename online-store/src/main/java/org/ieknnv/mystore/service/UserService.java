package org.ieknnv.mystore.service;

import org.ieknnv.mystore.entity.User;

import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUser(long userId);
}
