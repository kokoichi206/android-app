package jp.mydns.kokoichi206.g.pagination.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import jp.mydns.kokoichi206.g.pagination.data.local.BeerEntity
import jp.mydns.kokoichi206.g.pagination.data.mappers.toBeer
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BeerViewModel @Inject constructor(
    pager: Pager<Int, BeerEntity>,
) : ViewModel() {

    val beerPagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map { it.toBeer() }
        }
        .cachedIn(viewModelScope)
}