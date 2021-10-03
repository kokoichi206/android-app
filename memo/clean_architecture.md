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


```kotlin
_state.value = state.value.copy(
    isOrderSectionVisible = !state.value.isOrderSectionVisible
)
```


### Questions
- operator fun invoke()...
  - operator ?
- companion object ?
- sealed class

