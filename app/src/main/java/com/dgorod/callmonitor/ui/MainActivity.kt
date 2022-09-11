package com.dgorod.callmonitor.ui

import android.Manifest.permission.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.LinearLayoutManager
import com.dgorod.callmonitor.R
import com.dgorod.callmonitor.databinding.ActivityMainBinding
import com.dgorod.callmonitor.ui.adapter.CallsLogAdapter
import com.dgorod.callmonitor.ui.viewmodel.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Main app page activity.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSIONS_REQUEST = 1001
    }

    private val mainViewModel: MainActivityViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)

            ipLabel.text = mainViewModel.getServerHost()

            toggleButton.setOnClickListener {
                mainViewModel.toggleServerStatus()
            }

            callList.layoutManager = LinearLayoutManager(this@MainActivity)
            val listAdapter = CallsLogAdapter().also { callList.adapter = it }

            // Data observers
            mainViewModel.callsList.observe(this@MainActivity) {
                listAdapter.submitList(it)
            }
            mainViewModel.isServerRunning.observe(this@MainActivity) {
                toggleButton.setText(if(it) R.string.stop else R.string.start)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (checkSelfPermission(this, READ_CALL_LOG) == PERMISSION_GRANTED &&
            checkSelfPermission(this, READ_PHONE_STATE) == PERMISSION_GRANTED &&
            checkSelfPermission(this, READ_CONTACTS) == PERMISSION_GRANTED) {
            mainViewModel.fetchCallsLog()
        } else {
            val permissions = arrayOf(READ_CALL_LOG, READ_PHONE_STATE, READ_CONTACTS)
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST && grantResults[0] == PERMISSION_GRANTED) {
            mainViewModel.fetchCallsLog()
        }
    }
}