package com.example.progresstrackerbackend.controller;

import com.example.progresstrackerbackend.model.Goal;
import com.example.progresstrackerbackend.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal) {
        // --- YEH HAI ASLI FIX ---
        // Forcefully ID ko null set kar do taaki Hibernate isse hamesha naya object samjhe
        goal.setId(null);
        Goal savedGoal = goalRepository.save(goal);
        return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoalStatus(@PathVariable Long id, @RequestBody Goal goalDetails) {
        return goalRepository.findById(id).map(existingGoal -> {
            existingGoal.setCompleted(goalDetails.isCompleted());
            Goal updatedGoal = goalRepository.save(existingGoal);
            return ResponseEntity.ok(updatedGoal);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}