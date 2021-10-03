## Clean Architecture

### Three layers
- Presentor
  - Present something to the user
- Data
  - contains everything relevant to the data
  - API access, db access, shared preferences access
- Domain
  - connecting layer
  - buisiness logic
    - use_case package
      - how to access our repository

### memo
- repository
  - logic here?
  - which source do I have to return
    - cache or API?
  
### kotlin memo
- sortedByDescending
- DI using hilt
- エルビス演算子
  - message = e.message ?: "Coulnd't save note"
- `navController.navigateUp()`


```kotlin
_state.value = state.value.copy(
    isOrderSectionVisible = !state.value.isOrderSectionVisible
)
```

### androidx.compose.runtime.State
```kotlin
@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _noteTitle = mutableStateOf("")
    val noteTitle: State<String> = _noteTitle
}
```

### scope?
I wanna understand these code below

```kotlin
val scope = rememberCoroutineScope()
    ...
    .clickable {
        scope.launch {
            noteBackgroundAnimatable.animateTo(
                targetValue = Color(colorInt),
                animationSpec = tween(
                    durationMillis = 500
                )
            )
        }
    }
```


### Questions
- operator fun invoke()...
  - operator ?
- companion object ?
- sealed class
- scaffoldState ?
- scope.launch
