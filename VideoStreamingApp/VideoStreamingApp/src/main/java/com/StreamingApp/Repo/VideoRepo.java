package com.StreamingApp.Repo;

import com.StreamingApp.Entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VideoRepo extends JpaRepository<Video,Integer> {
    Optional<Video>findByTitle(String title);



}
