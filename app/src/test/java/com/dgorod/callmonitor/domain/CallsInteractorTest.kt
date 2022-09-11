package com.dgorod.callmonitor.domain

import android.Manifest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.dgorod.callmonitor.domain.model.Call
import com.dgorod.callmonitor.fakes.FakeCallsRepository
import com.dgorod.callmonitor.ui.model.CallUiModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CallsInteractorTest {

    @get:Rule
    val rule = GrantPermissionRule.grant(Manifest.permission.READ_CALL_LOG)

    private val testContext = InstrumentationRegistry.getInstrumentation().context

    private lateinit var callsRepository: FakeCallsRepository
    private lateinit var underTest: CallsInteractor

    @Before
    fun setup() {
        callsRepository = FakeCallsRepository()
        underTest = CallsInteractor(testContext, callsRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `return mapped for UI list`() = runTest {
        val testCall = Call("test name", "123", 1000L, 100L)
        callsRepository.calls.add(testCall)

        val expected = listOf(CallUiModel(testCall))
        assertThat(underTest.getCallsList()).isEqualTo(expected)
    }
}