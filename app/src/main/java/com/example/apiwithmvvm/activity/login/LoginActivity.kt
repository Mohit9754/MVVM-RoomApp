package com.example.apiwithmvvm.activity.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.apitesting.basic.UtilityTools.Constants
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.UtilityTools.StatusCodeConstant
import com.example.apiwithmvvm.activity.dashboard.DashboardActivity
import com.example.apiwithmvvm.activity.signup.SignUpActivity
import com.example.apiwithmvvm.database.User
import com.example.apiwithmvvm.databinding.ActivityLoginBinding
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.model.LoginResponse
import com.example.apiwithmvvm.validation.Validation
import com.example.apiwithmvvm.validation.ValidationModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var activity = this@LoginActivity
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(activity, R.layout.activity_login)

        // Bind ViewModel to the layout
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = activity

        init()

    }

    private fun setObsOnValidationError() {

        viewModel.validationResult.observe(activity) { validationResult ->

            when (validationResult.tag) {

                Constants.userName -> Utils.showError(
                    activity,
                    validationResult,
                    binding.etUserName,
                    binding.tvUserNameErr
                )

                Constants.password -> Utils.showError(
                    activity,
                    validationResult,
                    binding.etPassword,
                    binding.tvPasswordErr
                )

                else -> {}
            }

        }
    }

    private fun setObsOnLoginResult() {

        val progressDialog = Utils.initProgressDialog(activity)

        viewModel.loginResult.observe(activity) { result ->

            when (result) {

                is ApiResult.Loading -> {

                    progressDialog.show()
                }

                is ApiResult.Success -> {

                    // Handle success
                    progressDialog.dismiss()

                    if (result.statusCode == StatusCodeConstant.OK) {

                        val user = result.data?.user

                        if (user != null) {

                            viewModel.insertUser(

                                User(
                                    userName = user.userName,
                                    name = user.name,
                                    email = user.email,
                                    profileImageUrl = user.profileImageUrl,
                                    token = user.token
                                )

                            )
                        }

                        Toast.makeText(activity, "${result.data?.message}", Toast.LENGTH_SHORT)
                            .show()

                        Utils.I_clear(activity, DashboardActivity::class.java, null)

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

    private fun handleApiError(result: ApiResult.Error<LoginResponse>) {

        when (result.statusCode) {

            StatusCodeConstant.BAD_REQUEST -> {

                Toast.makeText(activity, "${result.message}", Toast.LENGTH_SHORT).show()
            }

            StatusCodeConstant.UNAUTHORIZED -> {

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


    private fun setTextChangeLis() {

        Utils.addTextChangedListener(binding.etUserName, binding.tvUserNameErr)

        Utils.addTextChangedListener(binding.etPassword, binding.tvPasswordErr)

    }

    private fun setFocusChangeLis() {

        Utils.setFocusChangeListener(activity, binding.etUserName, binding.mcvUserName)

        Utils.setFocusChangeListener(activity, binding.etPassword, binding.mcvPassword)

    }

    private fun init() {

        setTextChangeLis()

        setFocusChangeLis()

        setObsOnValidationError()

        setObsOnLoginResult()

        binding.tvSignUp.setOnClickListener(activity)

        binding.mcvLogin.setOnClickListener(activity)

        binding.ivPasswordVisibility.setOnClickListener(activity)


    }

    override fun onClick(view: View?) {

        when (view) {

            binding.tvSignUp -> {

                Utils.I(activity, SignUpActivity::class.java, null)
            }

            binding.mcvLogin -> {

                val validationModelList: MutableList<ValidationModel> = ArrayList()

                validationModelList.add(
                    ValidationModel(
                        Validation.Type.Empty,
                        binding.etUserName.text.toString().trim(),
                        Constants.userName,
                    )
                )

                validationModelList.add(
                    ValidationModel(
                        Validation.Type.Empty,
                        binding.etPassword.text.toString().trim(),
                        Constants.password,
                    )
                )

                viewModel.validate(validationModelList)

            }

            binding.ivPasswordVisibility -> {

                if (!isPasswordVisible) {

                    isPasswordVisible = true
                    binding.ivPasswordVisibility.setImageResource(R.drawable.ic_hide)
                    binding.etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    binding.etPassword.setSelection(binding.etPassword.length())

                } else {
                    isPasswordVisible = false
                    binding.ivPasswordVisibility.setImageResource(R.drawable.ic_show)
                    binding.etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    binding.etPassword.setSelection(binding.etPassword.length())
                }

            }


        }

    }
}