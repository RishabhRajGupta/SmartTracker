package com.example.progresstrackerbackend.repository;

import com.example.progresstrackerbackend.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// here functions will be created automatically which require sql queries
public interface GoalRepository extends JpaRepository<Goal, Long> {

}