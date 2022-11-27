package com.example.contactssampleapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val myWebView: WebView = findViewById(R.id.webview)
    myWebView.settings.javaScriptEnabled = true
    myWebView.loadUrl("https://contact-picker.glitch.me/")

    val contactPickerBtn: Button = findViewById(R.id.contact_picker_btn)
    contactPickerBtn.setOnClickListener{
      val intent = Intent(this, ContactPickerActivity::class.java)
      startActivity(intent)
    }
  }
}
