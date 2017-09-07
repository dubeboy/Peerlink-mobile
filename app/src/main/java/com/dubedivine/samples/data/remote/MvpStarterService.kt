package com.dubedivine.samples.data.remote


import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.data.model.PokemonListResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MvpStarterService {

    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int): Single<PokemonListResponse>

    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Single<Pokemon>

}
