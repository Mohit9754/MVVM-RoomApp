package com.example.apiwithmvvm.activity.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.activity.dashboard.DashboardActivity
import com.example.apiwithmvvm.activity.login.LoginActivity
import com.example.apiwithmvvm.databinding.ActivitySignUpBinding
import com.example.apiwithmvvm.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private val activity = this@SplashActivity
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(activity, R.layout.activity_splash)

        // Bind ViewModel to the layout
        binding.splashViewModel = viewModel
        binding.lifecycleOwner = activity

        init()
    }

    private fun init() {

        lifecycleScope.launch {

            delay(1000)

            val isUserLoggedIn = viewModel.isUserLogin()

            // Navigate based on the login status
            if (isUserLoggedIn) {
                Utils.I_clear(activity, DashboardActivity::class.java, null)
            } else {
                Utils.I_clear(activity, LoginActivity::class.java, null)
            }
        }
    }

}