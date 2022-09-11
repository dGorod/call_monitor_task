package com.dgorod.callmonitor.data

import android.Manifest
import android.content.ContentValues
import android.content.Context.TELECOM_SERVICE
import android.provider.CallLog.Calls
import android.telecom.TelecomManager
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.dgorod.callmonitor.data.repository.CallsRepositoryImpl
import com.dgorod.callmonitor.data.service.CallStatus
import com.dgorod.callmonitor.domain.model.Call
import com.dgorod.callmonitor.fakes.FakeContentProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowContentResolver

@RunWith(RobolectricTestRunner::class)
class CallsRepositoryImplTest {

    @get:Rule
    val rule = GrantPermissionRule.grant(Manifest.permission.READ_PHONE_STATE)

    private val testContext = InstrumentationRegistry.getInstrumentation().context

    private lateinit var telecomService: TelecomManager
    private lateinit var underTest: CallsRepositoryImpl

    @Before
    fun setup() {
        CallsQueryStorage.clear()
        telecomService = testContext.getSystemService(TELECOM_SERVICE) as TelecomManager
        underTest = CallsRepositoryImpl(testContext)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `return true ongoing call status when device is on call`() = runTest {
        shadowOf(telecomService).setIsInCall(true)

        assertThat(underTest.getOngoingCall()).isEqualTo(CallStatus(ongoing = true))
    }

    @Test
    fun `return calls list`() = runTest {
        val columns = arrayOf(Calls.CACHED_NAME, Calls.NUMBER, Calls.DATE, Calls.DURATION)
        ShadowContentResolver.registerProviderInternal(
            Calls.CONTENT_URI.authority,
            FakeContentProvider(columns)
        )

        val values = ContentValues(4).also {
            it.put(Calls.NUMBER, "123")
            it.put(Calls.CACHED_NAME, "test name")
            it.put(Calls.DATE, 1000L)
            it.put(Calls.DURATION, 100L)
        }

        val insertedUri = testContext.contentResolver.insert(Calls.CONTENT_URI, values)
        assertThat(insertedUri).isNotNull()

        val log = listOf(Call("test name", "123", 1000L, 100L))
        assertThat(underTest.getCalls()).isEqualTo(log)
    }
}