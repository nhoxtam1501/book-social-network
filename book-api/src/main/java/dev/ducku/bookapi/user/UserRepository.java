package dev.ducku.bookapi.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository {//extends JpaRepository<User, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "roles"
            })
    Optional<User> findByEmail(@Param("email") String email);
}
