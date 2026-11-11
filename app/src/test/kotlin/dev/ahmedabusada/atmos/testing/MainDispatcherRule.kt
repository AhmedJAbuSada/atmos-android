package dev.ahmedabusada.atmos.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * `viewModelScope` runs on `Dispatchers.Main`, which does not exist on the JVM. This rule
 * replaces it with a test dispatcher whose virtual clock the test controls, so a 300 ms
 * debounce is tested instantly with `advanceTimeBy(300)`.
 *
 * `setMain` is still marked experimental in kotlinx-coroutines; it is the official way to
 * test ViewModels, so the opt-in is limited to this one test helper.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val dispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}
