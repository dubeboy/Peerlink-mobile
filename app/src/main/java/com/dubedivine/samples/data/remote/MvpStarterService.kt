package com.dubedivine.samples.data.remote


import com.dubedivine.samples.data.model.Answer
import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.data.model.PokemonListResponse
import com.dubedivine.samples.data.model.Question
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MvpStarterService {

    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int): Single<PokemonListResponse>

    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Single<Pokemon>

    @GET("questions/search")
    fun getSearchSuggestions(@Query("text") charSequence: CharSequence): Single<List<Question>>

    @POST("questions/{q_id}/vote")
    fun addVoteToQuestion(@Path("q_id") qId: String, @Query("vote") vote: Int)  // should return a single Boolean

    @POST("questions/{q_id}/vote/{a_id}")
    fun addVoteToAnswer(@Path("q_id") questionId: String, @Path("q_id") id: Long, @Query("vote") vote: Int)

    @GET("answers/{q_id}")
    fun getMoreAnswers(@Path("q_id") questionId: String, @Query("page")  page: Int) : Single<List<Answer>>

}
