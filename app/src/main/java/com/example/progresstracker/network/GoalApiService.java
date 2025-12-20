package com.example.progresstracker.network;

import com.example.progresstracker.Goal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT; // <-- Naya import
import retrofit2.http.Path;

public interface GoalApiService {

    @GET("goals")
    Call<List<Goal>> getAllGoals();

    @POST("goals")
    Call<Goal> createGoal(@Body Goal goal);

    @DELETE("goals/{id}")
    Call<Void> deleteGoal(@Path("id") long id);

    // Endpoint to update a goal's completed status
    @PUT("goals/{id}")
    Call<Goal> updateGoalStatus(@Path("id") long id, @Body Goal goal);
}