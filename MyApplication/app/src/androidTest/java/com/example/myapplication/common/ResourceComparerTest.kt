package com.example.myapplication.common

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.R
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class ResourceComparerTest {

    // Don't do this in real practice !?
    private lateinit var resourceCompare: ResourceComparer

    // Before we run EVERY test case
    @Before
    fun setup() {
        resourceCompare = ResourceComparer()
    }

    // Clear Room DB, or something like that
    @After
    fun teardown() {
    }

    @Test
    fun stringResourceSameAsGivenString_returnsTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceCompare.isEqual(context, R.string.app_name, "My Application")
        assertThat(result).isTrue()
    }

    @Test
    fun stringResourceDifferentAsGivenString_returnsTrue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceCompare.isEqual(context, R.string.app_name, "My Hellow")
        assertThat(result).isFalse()
    }
}