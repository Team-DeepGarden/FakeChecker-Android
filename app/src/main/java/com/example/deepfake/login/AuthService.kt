package com.example.deepfake.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("api/auth/login")
    suspend fun logIn(
        @Body logInRequest: LogInRequest
    ): Response<LogInResponse?>

    @Headers("Content-Type: application/json")
    @POST("api/auth/signup")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): Response<SignUpResponse>

    @Headers("Content-Type: application/json")
    @POST("api/auth/checkid")
    suspend fun checkId(
        @Body checkIdRequest: CheckIdRequest
    ): Response<CheckIdResponse>

    @Headers("Content-Type: application/json")
    @POST("api/auth/guestlogin")
    suspend fun guestLogin(
        @Body guestLogInRequest: GuestLogInRequest
    ): Response<GuestLogInResponse>

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "api/auth/deleteAccount", hasBody = true)
    suspend fun signOut(
        @Body signOutRequest: SignOutRequest
    ): Response<SignOutResponse>
}
