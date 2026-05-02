package com.wificall.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class AudioStreamer {

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var isRunning = false

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 2

    fun start(hostAddress: String, isGroupOwner: Boolean) {
        isRunning = true

        if (isGroupOwner) {
            startServer()
        } else {
            startClient(hostAddress)
        }
    }

    private fun startServer() {
        thread {
            try {
                serverSocket = ServerSocket(AUDIO_PORT)
                clientSocket = serverSocket?.accept()
                startRecordingAndPlayback(clientSocket?.getOutputStream(), clientSocket?.getInputStream())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startClient(hostAddress: String) {
        thread {
            try {
                clientSocket = Socket(hostAddress, AUDIO_PORT)
                startRecordingAndPlayback(clientSocket?.getOutputStream(), clientSocket?.getInputStream())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startRecordingAndPlayback(outputStream: java.io.OutputStream?, inputStream: java.io.InputStream?) {
        if (outputStream == null || inputStream == null) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioTrack = AudioTrack(
            AudioManager.STREAM_VOICE_CALL,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            audioFormat,
            bufferSize,
            AudioTrack.MODE_STREAM
        )

        audioRecord?.startRecording()
        audioTrack?.play()

        val dataOutputStream = DataOutputStream(outputStream)
        val dataInputStream = DataInputStream(inputStream)
        val buffer = ByteArray(bufferSize)

        thread {
            while (isRunning) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    dataOutputStream.write(buffer, 0, read)
                    dataOutputStream.flush()
                }
            }
        }

        thread {
            while (isRunning) {
                val read = dataInputStream.read(buffer)
                if (read > 0) {
                    audioTrack?.write(buffer, 0, read)
                }
            }
        }
    }

    fun stop() {
        isRunning = false

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null

        clientSocket?.close()
        serverSocket?.close()
    }

    companion object {
        private const val AUDIO_PORT = 8888
    }
}
