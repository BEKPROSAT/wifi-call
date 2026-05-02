package com.wificall.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wificall.R
import com.wificall.network.WiFiP2PDevice

class DeviceAdapter(
    private val onDeviceClick: (WiFiP2PDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    private val devices = mutableListOf<WiFiP2PDevice>()

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.deviceName)
        private val addressText: TextView = itemView.findViewById(R.id.deviceAddress)

        fun bind(device: WiFiP2PDevice) {
            nameText.text = device.deviceName
            addressText.text = device.deviceAddress
            itemView.setOnClickListener { onDeviceClick(device) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int = devices.size

    fun addDevice(device: WiFiP2PDevice) {
        if (!devices.any { it.deviceAddress == device.deviceAddress }) {
            devices.add(device)
            notifyItemInserted(devices.size - 1)
        }
    }

    fun clear() {
        devices.clear()
        notifyDataSetChanged()
    }
}
