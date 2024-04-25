package br.com.signalr

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.TransportEnum


class MainActivity : AppCompatActivity() {
    private lateinit var layout: LinearLayout

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById<LinearLayout>(R.id.layout)

        //set server url eg:
        val hubConnection = HubConnectionBuilder.create("http://146.235.49.198#/chat")
            .withTransport(TransportEnum.LONG_POLLING)
            .build()

        hubConnection.onClosed {
            Log.d("Streaming", "conection close $it")
            runOnUiThread {
                addDateToLayout("conection close $it")
            }
        }

        hubConnection.start().subscribe({
            Log.d("Streaming", "connected to hub")
            hubConnection.stream(String::class.java, "Streaming").subscribe({ date ->
                runOnUiThread {
                    addDateToLayout(date)
                }
            }, { error ->
                Log.e("Streaming", "Error in streaming: ${error.message}")
                runOnUiThread {
                    addDateToLayout("Error: ${error.message}")
                }
            })
        }, { error ->
            Log.e("Streaming", "Error starting connection: ${error.message}")
            runOnUiThread {
                addDateToLayout("Connection Error: ${error.message}")
            }
        })
    }

    private fun addDateToLayout(date: String) {
        val textView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = date
            textSize = 16f
            setTextColor(Color.BLACK)
            setPadding(16, 16, 16, 16)
        }
        layout.addView(textView)
    }
}