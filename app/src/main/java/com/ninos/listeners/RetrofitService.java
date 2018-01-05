package com.ninos.listeners;

import com.ninos.models.AddPostResponse;
import com.ninos.models.Comment;
import com.ninos.models.CommentResponse;
import com.ninos.models.CommentsResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostResponse;
import com.ninos.models.Profile;
import com.ninos.models.ProfileResponse;
import com.ninos.models.RegisterResponse;
import com.ninos.models.UserCheckResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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
    Call<PostResponse> getPosts(@Query("from") int from, @Query("size") int size);

    @POST("posts")
    Call<AddPostResponse> addPost(@Body PostInfo postInfo, @Query("token") String token);

    @POST("refresh-token")
    Call<RegisterResponse> refreshToken(@Query("token") String token);

    @GET("posts/{postId}/comments")
    Call<CommentsResponse> getPostComments(@Path("postId") String postId, @Query("token") String token);

    @POST("posts/{postId}/comments")
    Call<CommentResponse> addPostComments(@Path("postId") String postId, @Query("token") String token, @Body Comment comment);
}
