package jp.mydns.kokoichi0206.pokemondetail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.mydns.kokoichi0206.data.data.remote.responses.Pokemon
import jp.mydns.kokoichi0206.data.repository.PokemonRepository
import jp.mydns.kokoichi0206.data.util.Resource
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}