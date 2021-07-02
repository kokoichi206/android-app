package io.kokoichi.sample.mastodonclient.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.kokoichi.sample.mastodonclient.R
import io.kokoichi.sample.mastodonclient.ui.toot_list.TootListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = TootListFragment()
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    fragment,
                    TootListFragment.TAG
                )
                .commit()
        }
    }
}