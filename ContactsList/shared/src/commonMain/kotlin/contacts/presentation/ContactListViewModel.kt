package contacts.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import contacts.domain.Contact
import contacts.domain.ContactDataSource
import contacts.domain.ContactValidator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactListViewModel(
    private val contactDataSource: ContactDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(ContactListState())
    val state = combine(
        _state,
        contactDataSource.getContacts(),
        contactDataSource.getRecentContacts(20),
    ) { state, contacts, recentContacts ->
        state.copy(
            contacts = contacts,
            recentlyAddedContacts = recentContacts,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ContactListState())

    var newContact: Contact? by mutableStateOf(null)
        private set

    fun onEvent(event: ContactListEvent) {
        when (event) {
            ContactListEvent.DeleteContact -> {
                viewModelScope.launch {
                    _state.value.selectedContact?.id?.let { id ->
                        _state.update {
                            it.copy(
                                isSelectedContactSheetOpen = false,
                            )
                        }
                        contactDataSource.deleteContact(id)

                        delay(300L)

                        _state.update {
                            it.copy(
                                selectedContact = null,
                            )
                        }
                    }
                }
            }

            ContactListEvent.DismissContact -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isSelectedContactSheetOpen = false,
                            isAddContactSheetOpen = false,
                            firstNameError = null,
                            lastNameError = null,
                            emailError = null,
                            phoneNumberError = null,
                        )
                    }
                    // Animation delay
                    delay(300L)

                    _state.update {
                        it.copy(
                            selectedContact = null
                        )
                    }
                }
            }

            is ContactListEvent.EditContact -> {
                _state.update {
                    it.copy(
                        selectedContact = null,
                        isAddContactSheetOpen = true,
                        isSelectedContactSheetOpen = false,
                    )
                }

                newContact = event.contact
            }

            ContactListEvent.OnAddNewContactClick -> {
                _state.update {
                    it.copy(
                        isAddContactSheetOpen = true
                    )
                }

                newContact = Contact(
                    id = null,
                    firstName = "",
                    lastName = "",
                    email = "",
                    phoneNumber = "",
                    photoBytes = null,
                )
            }
            // handle outside the viewmodel
            // ContactListEvent.OnAddPhotoClicked -> TODO()
            is ContactListEvent.OnEmailChanged -> {
                newContact = newContact?.copy(
                    email = event.value,
                )
            }

            is ContactListEvent.OnFirstNameChanged -> {
                newContact = newContact?.copy(
                    firstName = event.value,
                )
            }

            is ContactListEvent.OnLastNameChanged -> {
                newContact = newContact?.copy(
                    lastName = event.value,
                )
            }

            is ContactListEvent.OnPhoneNumberChanged -> {
                newContact = newContact?.copy(
                    phoneNumber = event.value,
                )
            }

            is ContactListEvent.OnPhotoPicked -> {
                newContact = newContact?.copy(
                    photoBytes = event.bytes,
                )
            }

            ContactListEvent.SaveContact -> {
                newContact?.let { contact ->
                    val result = ContactValidator.validateContact(contact)
                    val errors = listOfNotNull(
                        result.firstNameError,
                        result.lastNameError,
                        result.emailError,
                        result.phoneNumberError,
                    )

                    if (errors.isEmpty()) {
                        _state.update {
                            it.copy(
                                isAddContactSheetOpen = false,
                                firstNameError = null,
                                lastNameError = null,
                                emailError = null,
                                phoneNumberError = null
                            )
                        }

                        viewModelScope.launch {
                            contactDataSource.insertContact(contact)
                            delay(300L)
                            newContact = null
                        }
                    } else {
                        _state.update {
                            it.copy(
                                firstNameError = result.firstNameError,
                                lastNameError = result.lastNameError,
                                emailError = result.emailError,
                                phoneNumberError = result.phoneNumberError,
                            )
                        }
                    }
                }
            }

            is ContactListEvent.SelectContact -> {
                _state.update {
                    it.copy(
                        selectedContact = event.contact,
                        isSelectedContactSheetOpen = true,
                    )
                }
            }

            else -> Unit
        }
    }
}
