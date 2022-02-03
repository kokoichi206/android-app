package jp.mydns.kokoichi0206.memoryleak

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, SecondActivity::class.java).also {
            startActivity(it)
        }
    }

    companion object {
        lateinit var context: Context
    }
}
