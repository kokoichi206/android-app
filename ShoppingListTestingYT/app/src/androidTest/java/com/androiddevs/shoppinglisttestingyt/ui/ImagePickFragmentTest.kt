package com.androiddevs.shoppinglisttestingyt.ui

import androidx.fragment.app.FragmentFactory
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.filters.MediumTest
import com.androiddevs.shoppinglisttestingyt.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import javax.inject.Inject

@HiltAndroidTest
@MediumTest // Integration test
@ExperimentalCoroutinesApi
class ImagePickFragmentTest {

    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun clickImage_popBackStackAndSetImageUrl() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<ImagePickFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}