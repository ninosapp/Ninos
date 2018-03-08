package com.ninos.listeners;

import com.ninos.models.AddPostResponse;
import com.ninos.models.AllChallengeSearchResponse;
import com.ninos.models.ChallengeResponse;
import com.ninos.models.ChallengeSearchResponse;
import com.ninos.models.Comment;
import com.ninos.models.CommentResponse;
import com.ninos.models.CommentsResponse;
import com.ninos.models.EvaluateResponse;
import com.ninos.models.FollowingResponse;
import com.ninos.models.NotificationCount;
import com.ninos.models.NotificationResponse;
import com.ninos.models.PeopleResponse;
import com.ninos.models.PostClapResponse;
import com.ninos.models.PostInfo;
import com.ninos.models.PostReport;
import com.ninos.models.PostResponse;
import com.ninos.models.PostsResponse;
import com.ninos.models.Profile;
import com.ninos.models.ProfileResponse;
import com.ninos.models.QuestionResponse;
import com.ninos.models.QuizEvaluateBody;
import com.ninos.models.QuizEvaluateResultResponse;
import com.ninos.models.QuizResponse;
import com.ninos.models.QuizStartResponse;
import com.ninos.models.RegisterResponse;
import com.ninos.models.Response;
import com.ninos.models.UserCheckResponse;
import com.ninos.models.UserProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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

    @GET("/profile/settings")
    Call<ProfileResponse> getProfile(@Query("token") String token);

    @GET("/profile/users/{userId}")
    Call<UserProfileResponse> getUserProfile(@Path("userId") String userId, @Query("token") String token);

    @GET("/check/{userId}")
    Call<UserCheckResponse> userCheck(@Path("userId") String userId);

    @GET("/posts")
    Call<PostsResponse> searchPosts(@Query("from") int from, @Query("size") int size, @Query("token") String token);

    @GET("/posts")
    Call<PostsResponse> getUserPosts(@Query("from") int from, @Query("size") int size, @Query("userId") String userId, @Query("token") String token);

    @GET("/challenges")
    Call<ChallengeSearchResponse> searchChallenges(@Query("from") int from, @Query("size") int size, @Query("token") String token);

    @GET("/posts/{postId}")
    Call<PostResponse> getPost(@Path("postId") String postId, @Query("token") String token);

    @GET("/posts/{postId}")
    Call<PostResponse> getPost(@Path("postId") String postId);

    @POST("/posts")
    Call<AddPostResponse> addPost(@Body PostInfo postInfo, @Query("token") String token);

    @PATCH("/posts/{postId}")
    Call<AddPostResponse> updatePost(@Path("postId") String postId, @Body PostInfo postInfo, @Query("token") String token);

    @POST("/refresh-token")
    Call<RegisterResponse> refreshToken(@Query("token") String token);

    @GET("/posts/{postId}/comments")
    Call<CommentsResponse> getPostComments(@Path("postId") String postId, @Query("token") String token);

    @POST("/posts/{postId}/comments")
    Call<CommentResponse> addPostComments(@Path("postId") String postId, @Query("token") String token, @Body Comment comment);

    @PATCH("/posts/{postId}/comments")
    Call<CommentResponse> updatePostComment(@Path("postId") String postId, @Query("token") String token, @Body Comment comment);

    @DELETE("/posts/{postId}/comments/{commentId}")
    Call<Response> deleteComment(@Path("postId") String postId, @Path("commentId") String commentId, @Query("token") String token);

    @POST("/report-post")
    Call<Response> reportPost(@Body PostReport postReport, @Query("token") String token);

    @PUT("/posts/{postId}/claps")
    Call<PostClapResponse> addPostClaps(@Path("postId") String postId, @Query("token") String token);

    @DELETE("/posts/{postId}/claps")
    Call<PostClapResponse> removePostClaps(@Path("postId") String postId, @Query("token") String token);

    @GET("/quizzes-active")
    Call<QuizResponse> getActiveQuizzes(@Query("from") int from, @Query("size") int size, @Query("token") String token);

    @GET("/quizzes-completed")
    Call<QuizResponse> getCompletedQuizzes(@Query("from") int from, @Query("size") int size, @Query("token") String token);

    @GET("/users")
    Call<PeopleResponse> searchUsers(@Query("from") int from, @Query("size") int size, @Query("userName") String userName, @Query("token") String token);

    @GET("/search-posts")
    Call<AllChallengeSearchResponse> searchChallenges(@Query("from") int from, @Query("size") int size, @Query("keyword") String keyword, @Query("token") String token);

    @GET("/search-challenge")
    Call<ChallengeSearchResponse> searchAllChallenges(@Query("from") int from, @Query("size") int size, @Query("keyword") String keyword, @Query("token") String token);

    @GET("/quizzes/{quizId}/questions")
    Call<QuestionResponse> getQuiz(@Path("quizId") String quizId, @Query("token") String token);

    @PUT("/quizzes/{quizId}/start")
    Call<QuizStartResponse> startQuiz(@Path("quizId") String quizId, @Query("token") String token);

    @POST("/quizzes/{quizId}/evaluate")
    Call<EvaluateResponse> evaluateResult(@Path("quizId") String quizId, @Body QuizEvaluateBody body, @Query("token") String token);

    @GET("/quizzes/{quizId}/user/{userId}/evalutions")
    Call<QuizEvaluateResultResponse> getQuizResult(@Path("quizId") String quizId, @Path("userId") String userId, @Query("token") String token);

    @GET("/challenges/{challengeId}")
    Call<ChallengeResponse> getChallenge(@Path("challengeId") String challengeId, @Query("token") String token);

    @PATCH("/profile/settings")
    Call<Response> updateProfile(@Body Profile profile, @Query("token") String token);

    @GET("/posts")
    Call<PostsResponse> getChallenges(@Query("from") int from, @Query("size") int size, @Query("type") String type, @Query("challengeId") String challengeId, @Query("token") String token);

    @PUT("/follow/{followingId}")
    Call<Response> follow(@Path("followingId") String followingId, @Query("token") String token);

    @DELETE("/follow/{followingId}")
    Call<Response> unFollow(@Path("followingId") String followingId, @Query("token") String token);

//    @DELETE("/posts/{postId}/{isChallenge}")
//    Call<Response> deletePost(@Path("postId") String postId, @Path("isChallenge") boolean isChallenge, @Query("token") String token);

    @DELETE("/posts/{postId}")
    Call<Response> deletePost(@Path("postId") String postId, @Query("token") String token);

    @GET("/following-users")
    Call<FollowingResponse> getFollowing(@Query("token") String token);

    @GET("/followers-users")
    Call<FollowingResponse> getFollowers(@Query("token") String token);

    @GET("/notifications")
    Call<NotificationResponse> getNotifications(@Query("token") String token);

    @GET("/notifications-count")
    Call<NotificationCount> getNotificationsCount(@Query("token") String token);

    @PUT("/notifications/{notificationId}/mark")
    Call<Response> markNotificationAsRead(@Path("notificationId") String notificationId, @Query("token") String token);

    @PATCH("/notifications/mark-all")
    Call<Response> markAllNotificationsAsRead(@Query("token") String token);
}
