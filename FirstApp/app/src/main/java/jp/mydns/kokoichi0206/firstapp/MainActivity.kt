package jp.mydns.kokoichi0206.firstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.myButton)
        btn.setOnClickListener {
            Log.d("hoge", "btn clicked")

            val textView = findViewById<TextView>(R.id.textView)
            textView.text = "Yeeeeeeeaaahhh"

            Toast.makeText(this, "Toast Toast !", Toast.LENGTH_LONG).show()
        }
    }
}