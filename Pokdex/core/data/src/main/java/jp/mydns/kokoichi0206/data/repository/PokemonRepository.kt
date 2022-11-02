package jp.mydns.kokoichi0206.data.repository

import jp.mydns.kokoichi0206.data.data.remote.responses.PokemonList
import dagger.hilt.android.scopes.ActivityScoped
import jp.mydns.kokoichi0206.data.util.Resource
import java.lang.Exception
import javax.inject.Inject


/**
 * 注意：
 * 　普通はここは Interface にする！
 * 　他のリポジトリを参考にする＠！！
 */
@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: jp.mydns.kokoichi0206.data.data.remote.PokeApi
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<jp.mydns.kokoichi0206.data.data.remote.responses.Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred")
        }
        return Resource.Success(response)
    }
}