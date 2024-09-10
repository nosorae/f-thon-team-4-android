package com.yessorae.yabaltravel.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.yessorae.yabaltravel.R
import com.yessorae.yabaltravel.databinding.ActivityIntroBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private val viewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observer()
        addListener()
    }
    private fun observer(){
        viewModel.liveData.observe(this, Observer {
            if (!it) {
                Toast.makeText(this, getString(R.string.msg_setUp_error), Toast.LENGTH_SHORT).show()
                return@Observer
            }
            Log.d(this.javaClass.name, "Success to check Internet and Sensor go to main page")
            val intent = Intent(this , MainActivity :: class.java)
            startActivity(intent)
        })
    }

    private fun addListener() {
        binding.btnStart.setOnClickListener {
            viewModel.getSettingValue()
        }
    }

}