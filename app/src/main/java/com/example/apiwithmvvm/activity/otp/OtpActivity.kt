package com.example.apiwithmvvm.activity.otp

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.apitesting.basic.UtilityTools.Constants
import com.example.apitesting.basic.UtilityTools.Constants.Companion.email
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.UtilityTools.StatusCodeConstant
import com.example.apiwithmvvm.activity.dashboard.DashboardActivity
import com.example.apiwithmvvm.activity.login.LoginActivity
import com.example.apiwithmvvm.activity.login.LoginViewModel
import com.example.apiwithmvvm.database.User
import com.example.apiwithmvvm.databinding.ActivityLoginBinding
import com.example.apiwithmvvm.databinding.ActivityOtpBinding
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.model.LoginResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOtpBinding
    private val viewModel: OtpViewModel by viewModels()
    private var activity = this@OtpActivity
    private var email:String? = null
    private var _id:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(activity, R.layout.activity_otp)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Bind ViewModel to the layout
        binding.optViewModel = viewModel
        binding.lifecycleOwner = activity

        init()

    }

    private fun setTextChangeLisOnPinView() {

        binding.pinView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(otp: Editable?) {

                if (otp.toString().length > 5) {
                    Utils.enableButton(binding.mcvVerifyOtp)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    private fun setEmail() {

        val bundle = intent.extras
        email = bundle?.getString(Constants.email)
        _id = bundle?.getString(Constants._id)

        binding.tvEmail.text = email
    }

    private fun setObsOnVerifyOTPResult() {

        val progressDialog = Utils.initProgressDialog(activity)

        viewModel.verifyOTPResult.observe(activity) { result ->

            when (result) {

                is ApiResult.Loading -> {

                    progressDialog.show()
                }

                is ApiResult.Success -> {

                    // Handle success
                    progressDialog.dismiss()

                    if (result.statusCode == StatusCodeConstant.OK) {

                        Toast.makeText(activity, "${result.data?.message}", Toast.LENGTH_SHORT)
                            .show()

                        Utils.I_clear(activity, LoginActivity::class.java, null)

                    }

                }

                is ApiResult.Error -> {

                    // Handle error
                    progressDialog.dismiss()

                    handleApiError(result)

                }

                else -> {}
            }
        }
    }

    private fun setObsOnResendOTPResult() {

        val progressDialog = Utils.initProgressDialog(activity)

        viewModel.resendOTPResult.observe(activity) { result ->

            when (result) {

                is ApiResult.Loading -> {

                    progressDialog.show()
                }

                is ApiResult.Success -> {

                    // Handle success
                    progressDialog.dismiss()

                    if (result.statusCode == StatusCodeConstant.OK) {

                        Toast.makeText(activity, "${result.data?.message}", Toast.LENGTH_SHORT)
                            .show()

                        setTimer()

                        binding.tvMessage.visibility = View.VISIBLE

                    }

                }

                is ApiResult.Error -> {

                    // Handle error
                    progressDialog.dismiss()

                    handleApiError(result)

                }

                else -> {}
            }
        }
    }


    private fun handleApiError(result: ApiResult.Error<ApiResponse>) {

        when (result.statusCode) {

            StatusCodeConstant.BAD_REQUEST -> {

                Toast.makeText(activity, "${result.message}", Toast.LENGTH_SHORT).show()
            }

            StatusCodeConstant.UNAUTHORIZED -> {

                Toast.makeText(activity, "${result.message}", Toast.LENGTH_SHORT).show()
            }

            StatusCodeConstant.CONFLICT -> {

                Toast.makeText(activity, "${result.message}", Toast.LENGTH_SHORT).show()
            }

            StatusCodeConstant.TOO_MANY_REQUESTS -> {

                Toast.makeText(activity, "${result.message}", Toast.LENGTH_SHORT).show()
            }

            else -> {

                Toast.makeText(
                    activity,
                    getString(R.string.unknown_error, result.statusCode.toString()),
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun setTimer() {

        binding.tvResendOTP.visibility = View.GONE
        binding.llTimer.visibility = View.VISIBLE

        val timerDuration = 60000L

        object : CountDownTimer(timerDuration, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                val seconds = millisUntilFinished / 1000
                binding.tvTime.text = String.format("00:%02d", seconds)
            }

            override fun onFinish() {

                binding.tvTime.text  = activity.getString(R.string._00_00)
                binding.llTimer.visibility = View.GONE
                binding.tvResendOTP.visibility = View.VISIBLE
            }

        }.start()

    }

    private fun init() {

        Utils.disableButton(binding.mcvVerifyOtp)

        setEmail()

        setTimer()

        setTextChangeLisOnPinView()

        setObsOnVerifyOTPResult()

        setObsOnResendOTPResult()

        binding.mcvVerifyOtp.setOnClickListener(activity)

        binding.tvResendOTP.setOnClickListener(activity)

    }

    override fun onClick(view: View?) {

        when (view) {

            binding.mcvVerifyOtp -> {
                _id?.let { viewModel.verifyOTP(it,binding.pinView.text.toString()) }
            }

            binding.tvResendOTP -> {
                _id?.let { viewModel.resendOTP(it) }
            }

        }

    }

}