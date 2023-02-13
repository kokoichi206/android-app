package jp.mydns.kokoichi0206.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import jp.mydns.kokoichi0206.search.ui.theme.SearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchTheme {
                val viewModel = viewModel<MainViewModel>()
                val searchText by viewModel.searchText.collectAsState()
                val persons by viewModel.persons.collectAsState()
                val isSearching by viewModel.isSearching.collectAsState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = viewModel::onSearchTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {Text(text="Search")},
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        items(persons) { person ->
                            Text(
                                text = "${person.firstName} ${person.lastName}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
