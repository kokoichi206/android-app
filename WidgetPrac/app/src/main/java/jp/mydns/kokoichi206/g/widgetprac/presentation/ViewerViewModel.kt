package jp.mydns.kokoichi206.g.widgetprac.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.mydns.kokoichi206.g.widgetprac.domain.GetUserUsecase
import jp.mydns.kokoichi206.g.widgetprac.domain.SimpleUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    private val getUserUsecase: GetUserUsecase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    data class UserState(
        val user: SimpleUser? = null,
        val isLoading: Boolean = false,
    )

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                )
            }
            _state.update {
                it.copy(
                    user = getUserUsecase.execute(),
                    isLoading = false,
                )
            }
        }
    }
}