package jp.mydns.kokoichi0206.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
class MainViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _persons = MutableStateFlow(allPersons)
    val persons = searchText
        // Delay before other blocks are executed
        // Like to avoid continuous network communication.
        .debounce(400L)
        .onEach { _isSearching.update { true } }
        .combine(_persons) { text, persons ->
            // このブロックは searchText or _persons が変化した時に呼び出される
            if (text.isBlank()) {
                persons
            } else {
                // Simulate network call.
                delay(1000L)
                persons.filter {
                    it.doesMatchSearchQuery(query = text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _persons.value,
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}

data class Person(
    val firstName: String,
    val lastName: String,
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$firstName$lastName",
            "$firstName $lastName",
            "${firstName.first()}${lastName.first()}",
            "${firstName.first()} ${lastName.first()}",
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allPersons = listOf(
    Person(
        firstName = "John",
        lastName = "Doe",
    ),
    Person(
        firstName = "Peter",
        lastName = "Pan",
    ),
    Person(
        firstName = "Foo",
        lastName = "Bar",
    ),
    Person(
        firstName = "Hoge",
        lastName = "Pien",
    ),
)
