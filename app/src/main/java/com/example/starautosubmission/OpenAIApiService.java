package com.example.starautosubmission;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApiService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: sk-proj-MUJbNCHPAeJWtiG2cDL8T3BlbkFJh6Y8cyOrdMbXk70zAAVr"
    })
    @POST("v1/images:describe")
    Call<OpenAIResponse> describeImage(@Body OpenAIRequest request);
}
