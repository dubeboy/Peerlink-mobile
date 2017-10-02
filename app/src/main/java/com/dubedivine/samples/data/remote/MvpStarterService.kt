package com.dubedivine.samples.data.remote


import com.dubedivine.samples.data.model.*
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface MvpStarterService {

    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int): Single<PokemonListResponse>

    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Single<Pokemon>

    @GET("questions/search")
    fun getSearchSuggestions(@Query("text") charSequence: CharSequence): Single<List<Question>>

    @POST("questions/{q_id}/vote")
    fun addVoteToQuestion(@Path("q_id") qId: String, @Query("vote") vote: Int): Single<StatusResponse>  // should return a single Boolean

    @POST("questions/{q_id}/vote/{a_id}")
    fun addVoteToAnswer(@Path("q_id") questionId: String, @Path("q_id") id: Long, @Query("vote") vote: Int): Single<Status>

    @GET("answers/{q_id}")
    fun getMoreAnswers(@Path("q_id") questionId: String, @Query("page")  page: Int) : Single<List<Answer>>

    @GET("tags/suggest")
    fun getTagSuggestion(@Query("tag") tag: CharSequence): Single<List<Tag>>

    //since we are adding a new question we want to put!!
    @PUT("questions")
    fun postQuestion(question: Question, retrofitFileParts: MutableList<MultipartBody.Part>):  Single<StatusResponse>

}
