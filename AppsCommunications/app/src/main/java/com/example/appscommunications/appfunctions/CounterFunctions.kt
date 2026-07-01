package com.example.appscommunications.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.example.appscommunications.ui.CounterViewModel

class CounterFunctions(
    private val counterViewModel: CounterViewModel,
) {

    /**
     * Increases the counter by a given amount.
     *
     * Use this when the user wants to add to, raise, bump up, or increase the counter.
     *
     * @param by The amount to add to the counter. Defaults to 1 when the user does not give a number.
     * @return A short confirmation describing the counter's new value.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun incrementCounter(appFunctionContext: AppFunctionContext, by: Int = 1): String {
        val newValue = counterViewModel.increment(by)
        return "Counter increased by $by. It is now $newValue."
    }

    /**
     * Decreases the counter by a given amount.
     *
     * Use this when the user wants to subtract from, lower, reduce, or decrease the counter.
     *
     * @param by The amount to subtract from the counter. Defaults to 1 when the user does not give a number.
     * @return A short confirmation describing the counter's new value.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun decrementCounter(appFunctionContext: AppFunctionContext, by: Int = 1): String {
        val newValue = counterViewModel.decrement(by)
        return "Counter decreased by $by. It is now $newValue."
    }

    /**
     * Resets the counter back to zero.
     *
     * Use this when the user wants to reset, clear, or start the counter over.
     *
     * @return A short confirmation that the counter was reset.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun resetCounter(appFunctionContext: AppFunctionContext): String {
        counterViewModel.reset()
        return "Counter has been reset to 0."
    }

    /**
     * Returns the counter's current value.
     *
     * Use this when the user asks what the counter is, how high it is, or for its current value.
     *
     * @return The current value of the counter.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun getCounter(appFunctionContext: AppFunctionContext): Int {
        return counterViewModel.current()
    }
}