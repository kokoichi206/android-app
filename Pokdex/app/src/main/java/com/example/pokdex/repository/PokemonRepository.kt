package com.example.pokdex.repository

import com.example.pokdex.data.remote.PokeApi
import com.example.pokdex.data.remote.responses.Pokemon
import com.example.pokdex.data.remote.responses.PokemonList
import com.example.pokdex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.Exception
import javax.inject.Inject


/**
 * 注意：
 * 　普通はここは Interface にする！
 * 　他のリポジトリを参考にする＠！！
 */
@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred")
        }
        return Resource.Success(response)
    }
}