package com.cooksys.socialmedia.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cooksys.socialmedia.entities.User;
import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    List<User> findAllByDeletedFalse();

    Optional<User> findByCredentialsUsername(String username);

    Optional<User> findByCredentialsUsernameAndCredentialsPassword(String username, String password);
}
