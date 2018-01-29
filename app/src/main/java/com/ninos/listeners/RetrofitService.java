package com.ninos.listeners;

import com.ninos.models.AddPostResponse;
import com.ninos.models.ChallengeSearchResponse;
import com.ninos.models.Comment;
import com.ninos.models.CommentResponse;
import com.ninos.models.CommentsResponse;
import com.ninos.models.PeopleResponse;
import com.ninos.models.PostClapResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostReport;
import com.ninos.models.PostResponse;
import com.ninos.models.PostSearchResponse;
import com.ninos.models.PostsResponse;
import com.ninos.models.Profile;
import com.ninos.models.ProfileResponse;
import com.ninos.models.QuizResponse;
import com.ninos.models.RegisterResponse;
import com.ninos.models.Response;
import com.ninos.models.UserCheckResponse;
import com.ninos.models.UserInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by FAMILY on 14-12-2017.
 */

public interface RetrofitService {
    @POST("register")
    Call<RegisterResponse> registerChild(@Body Profile profile);

    @GET("register")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @GET("/profile/users/{userId}")
    Call<ProfileResponse> getUserProfile(@Header("Authorization") String token, @Path("userId") String userId);

    @GET("check/{userId}")
    Call<UserCheckResponse> userCheck(@Path("userId") String userId);

    @GET("posts")
    Call<PostsResponse> getPosts(@Query("from") int from, @Query("size") int size, @Query("token") String token);

    @GET("challenges")
    Call<ChallengeSearchResponse> getChallenges(@Query("from") int from, @Query("size") int size, @Query("token") String token);

    @GET("posts/{postId}")
    Call<PostResponse> getPost(@Path("postId") String postId, @Query("token") String token);

    @POST("posts")
    Call<AddPostResponse> addPost(@Body PostInfo postInfo, @Query("token") String token);

    @PATCH("posts/{postId}")
    Call<AddPostResponse> updatePost(@Path("postId") String postId, @Body PostInfo postInfo, @Query("token") String token);

    @POST("refresh-token")
    Call<RegisterResponse> refreshToken(@Query("token") String token);

    @GET("posts/{postId}/comments")
    Call<CommentsResponse> getPostComments(@Path("postId") String postId, @Query("token") String token);

    @POST("posts/{postId}/comments")
    Call<CommentResponse> addPostComments(@Path("postId") String postId, @Query("token") String token, @Body Comment comment);

    @POST("report-post")
    Call<Response> reportPost(@Body PostReport postReport, @Query("token") String token);

    @PUT("/posts/{postId}/claps")
    Call<PostClapResponse> addPostClaps(@Path("postId") String postId, @Query("token") String token);

    @GET("quizzes")
    Call<QuizResponse> getQuizzes(@Query("token") String token);

    @GET("users")
    Call<PeopleResponse> getUsers(@Query("from") int from, @Query("size") int size, @Query("userName") String userName, @Query("token") String token);

    @GET("/search-posts")
    Call<PostSearchResponse> getPosts(@Query("from") int from, @Query("size") int size, @Query("keyword") String keyword, @Query("token") String token);

    @GET("/search-challenge")
    Call<ChallengeSearchResponse> getChallenges(@Query("from") int from, @Query("size") int size, @Query("keyword") String keyword, @Query("token") String token);

    @PATCH("profile/settings")
    Call<Response> updateProfile(@Body UserInfo userInfo, @Query("token") String token);

}
