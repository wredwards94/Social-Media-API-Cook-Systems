package com.cooksys.socialmedia.repositories;

import com.cooksys.socialmedia.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findHashtagByLabel(String label);

    Optional<Hashtag>findByLabel(String label);

    // Custom query method to find hashtags by label
    @Query("SELECT h FROM Hashtag h WHERE h.label = :label")
    Optional<Hashtag> findHashtagsByLabel(@Param("label") String label);
}
