package dev.ducku.bookapi.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository {//extends JpaRepository<Role, Integer> {

    //Optional<Role> findByName(@Param("roleName")String roleName);
}
