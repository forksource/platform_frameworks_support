/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.navigation.test.R
import androidx.navigation.testing.TestNavigator
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.spy

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavControllerActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(NavControllerActivity::class.java)

    private lateinit var navController: NavController
    private lateinit var navigator: TestNavigator

    @Before
    fun setup() {
        navController = NavController(activityRule.activity)
        navigator = TestNavigator()
        navController.navigatorProvider.addNavigator(navigator)
        TargetActivity.instances = spy(ArrayList())
    }

    @Test
    fun testNavigateUpPop() {
        navController.setGraph(R.navigation.nav_simple)
        navController.navigate(R.id.second_test)
        assertThat(navController.currentDestination?.id)
            .isEqualTo(R.id.second_test)
        assertThat(navigator.backStack.size)
            .isEqualTo(2)

        assertThat(navController.navigateUp())
            .isTrue()
        assertThat(navController.currentDestination?.id)
            .isEqualTo(R.id.start_test)
        assertThat(navigator.backStack.size)
            .isEqualTo(1)
    }

    @Test
    fun testNavigateUp() {
        navController.setGraph(R.navigation.nav_simple)
        navController.handleDeepLink(Intent().apply {
            data = Uri.parse("android-app://androidx.navigation.test/test")
        })
        assertThat(navController.currentDestination?.id)
            .isEqualTo(R.id.second_test)
        assertThat(navigator.backStack.size)
            .isEqualTo(1)

        assertThat(activityRule.activity.isFinishCalled)
            .isFalse()
        assertThat(navController.navigateUp())
            .isTrue()
        assertThat(activityRule.activity.isFinishCalled)
            .isTrue()
    }
}

class NavControllerActivity : Activity() {
    var isFinishCalled = false

    override fun finish() {
        super.finish()
        isFinishCalled = true
    }
}
