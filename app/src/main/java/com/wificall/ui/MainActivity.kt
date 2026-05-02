package com.wificall.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wificall.R
import com.wificall.service.WiFiDirectService

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var discoverButton: Button
    private lateinit var hangupButton: Button
    private lateinit var devicesRecyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter

    private val wifiDirectService = WiFiDirectService()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            initializeWiFiDirect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        checkPermissions()
    }

    private fun initViews() {
        statusText = findViewById(R.id.statusText)
        discoverButton = findViewById(R.id.discoverButton)
        hangupButton = findViewById(R.id.hangupButton)
        devicesRecyclerView = findViewById(R.id.devicesRecyclerView)

        deviceAdapter = DeviceAdapter { device ->
            wifiDirectService.connectToDevice(device)
        }
        devicesRecyclerView.layoutManager = LinearLayoutManager(this)
        devicesRecyclerView.adapter = deviceAdapter

        discoverButton.setOnClickListener {
            startDiscovery()
        }

        hangupButton.setOnClickListener {
            endCall()
        }

        wifiDirectService.onStatusChanged = { status ->
            statusText.text = status
        }

        wifiDirectService.onDeviceDiscovered = { device ->
            deviceAdapter.addDevice(device)
        }

        wifiDirectService.onCallStateChanged = { inCall ->
            hangupButton.isEnabled = inCall
            discoverButton.isEnabled = !inCall
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            initializeWiFiDirect()
        }
    }

    private fun initializeWiFiDirect() {
        wifiDirectService.initialize(this)
    }

    private fun startDiscovery() {
        deviceAdapter.clear()
        wifiDirectService.startDiscovery()
    }

    private fun endCall() {
        wifiDirectService.endCall()
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiDirectService.cleanup()
    }
}
