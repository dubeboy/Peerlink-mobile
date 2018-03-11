package com.dubedivine.samples.data.remote


import com.dubedivine.samples.data.model.*
import io.reactivex.Single
import okhttp3.MultipartBody
import org.jetbrains.annotations.NotNull
import retrofit2.http.*

interface MvpStarterService {
    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int): Single<PokemonListResponse>

    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Single<Pokemon>

    @GET("questions/search")
    fun getSearchSuggestions(@Query("text") charSequence: CharSequence): Single<List<Question>>

    @POST("questions/{q_id}/vote")
    fun addVoteToQuestion(@Path("q_id") qId: String, @Query("vote") vote: Boolean): Single<StatusResponse<Boolean>>  // should return a single Boolean

    @POST("questions/{q_id}/vote/{a_id}/vote")
    fun addVoteToAnswer(@Path("q_id") questionId: String,
                        @Path("a_id") id: String?,
                        @Query("vote") vote: Boolean): Single<StatusResponse<Boolean>>

    @GET("answers/{q_id}")
    fun getMoreAnswers(@Path("q_id") questionId: String, @Query("page") page: Int): Single<List<Answer>>

    @GET("tags/suggest")
    fun getTagSuggestion(@Query("tag") tag: CharSequence): Single<List<Tag>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PUT("questions")
    fun postQuestion(@Body question: Question): Single<StatusResponse<Question>>

    @Multipart
    @POST("questions/{q_id}/files")
    fun postQuestionFiles(@Path("q_id") @NotNull questionId: String, @Part files: List<MultipartBody.Part>): Single<StatusResponse<Question>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PUT("questions/{q_id}/answer")
    fun postAnswer(@Path("q_id") questionId: String, @Body answer: Answer): Single<StatusResponse<Answer>>

    @Multipart
    @POST("questions/{q_id}/answers/{a_id}/files")
    fun postAnswerFiles(@Path("q_id") questionId: String,
                        @Path("a_id") answerId: String,
                        @Part retrofitFileParts: List<MultipartBody.Part>): Single<StatusResponse<Answer>>

    @POST("questions/{q_id}/comment")
    fun postCommentQuestion(@Path("q_id") questionId: String,
                            @Body body: Comment): Single<StatusResponse<Comment>>

    @POST("questions/{q_id}/answer/{a_id}/comment")
    fun postCommentForAnswer(@Path("q_id") questionId: String,
                             @Path("a_id") answerId: String,
                             @Body body: Comment): Single<StatusResponse<Comment>>
}
