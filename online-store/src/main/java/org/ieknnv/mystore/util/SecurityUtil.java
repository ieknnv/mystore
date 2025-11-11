package org.ieknnv.mystore.util;

import org.ieknnv.mystore.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SecurityUtil {

    public Mono<Optional<Long>> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(sc -> {
                    Authentication auth = sc.getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof User p) {
                        return Mono.just(Optional.of(p.getId()));
                    }
                    return Mono.just(Optional.<Long>empty());
                })
                .defaultIfEmpty(Optional.empty());
    }

}
