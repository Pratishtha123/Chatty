package com.paru.chatty.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.paru.chatty.R

class Splash_Activity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)


        Handler().postDelayed({
            val intent= Intent(this@Splash_Activity2, WelcomeActivity::class.java)
            startActivity(intent)
        },5000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
