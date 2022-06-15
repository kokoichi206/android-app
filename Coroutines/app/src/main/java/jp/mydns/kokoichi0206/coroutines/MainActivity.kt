package jp.mydns.kokoichi0206.coroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = MainViewModel()

        // this causes null po
        // findViewById<TextView>(R.id.second).text = "hoge"
        findViewById<TextView>(R.id.second)?.let {
            it.text = "hoge"
        }

        findViewById<TextView>(R.id.hiWorld).apply {
            setOnClickListener {
                // GlobalScope will not be destroyed
                //GlobalScope.launch {
                lifecycleScope.launch {
                    while (true) {
                        delay(1000L)
                        Log.d(TAG, "Still running...")
                    }
                }
                GlobalScope.launch {
                    delay(5000L)
                    Intent(this@MainActivity, SecondActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                    Log.d(TAG, "Still running...")
                }
            }
        }
        return

        GlobalScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                // Async and await
                val answer1 = async { doNetworkCall() }
                val answer2 = async { doNetworkCall2() }
                Log.d(TAG, "Answer1 is ${answer1.await()}")
                Log.d(TAG, "Answer2 is ${answer2.await()}")
            }
            Log.d(TAG, "Requests took $time ms.")
        }

        val calcJob = GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "Starting long running calc ...")
            withTimeout(3000L) {
            }
            // Be careful if the func is not suspend ?
            // or always ?
            for (i in 30..40) {
                // This fix the problem
                if (isActive) {
                    Log.d(TAG, "Result for i = $i: ${fib(i)}")
                }
            }
            Log.d(TAG, "Ending long running calc ...")
        }

        runBlocking {
            delay(2000L)
            calcJob.cancel()
            Log.d(TAG, "Job was canceled!")
            delay(10000L)
        }

        // coroutine task always return a job
        val job = GlobalScope.launch(Dispatchers.Default) {
            repeat(5) {
                Log.d(TAG, "Coroutine is still working...")
                delay(1000L)
            }
        }
        runBlocking {
            job.join()
            job.cancel()
            Log.d(TAG, "join finished!")
        }

        // GlobalScope.launch(Dispatchers.Main) will NOT block the main thread,
        // but runBlocking will.
        runBlocking {
            launch(Dispatchers.IO) {
                delay(10000L)
                Log.d(TAG, "Loooong delay")
            }
            launch(Dispatchers.IO) {
                delay(10000L)
                Log.d(TAG, "Loooong delay async")
            }
            delay(100L)
        }
        Log.d(TAG, "runBlocking finished")

        // Coroutine Contexts
        // Dispatchers.Main: UI thread operation
        // Dispatchers.IO: Network connection, Database access
        // Dispatchers.Default: Long calculation, or ..
        // Dispatchers.Unconfined:
        // newSingleThreadContext("MyThread")
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Starting coroutine in thread ${Thread.currentThread().name}")
            val answer = doNetworkCall()
            // switch the context to the main dispatcher
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Setting text in thread ${Thread.currentThread().name}")
                findViewById<TextView>(R.id.hiWorld).text = answer
            }
        }

        GlobalScope.launch {
            val ans = doNetworkCall()
            val ans2 = doNetworkCall2()
            Log.d(TAG, ans)
            Log.d(TAG, ans2)
        }

        GlobalScope.launch {
            delay(3000L)
            Log.d(TAG, "Coroutine, thread ${Thread.currentThread().name}")
        }
        Log.d(TAG, "MainActivity, thread ${Thread.currentThread().name}")
    }

    suspend fun doNetworkCall(): String {
        delay(3000L)
        return "200"
    }

    suspend fun doNetworkCall2(): String {
        delay(3000L)
        return "404"
    }

    fun fib(i: Int): Long {
        return if (i == 0) 0
        else if (i == 1) 1
        else fib(i - 1) + fib(i - 2)
    }
}