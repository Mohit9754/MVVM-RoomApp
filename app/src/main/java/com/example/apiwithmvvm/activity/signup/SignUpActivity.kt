package com.example.apiwithmvvm.activity.signup

import android.Manifest
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import com.example.apitesting.basic.UtilityTools.Constants
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.UtilityTools.StatusCodeConstant
import com.example.apiwithmvvm.activity.login.LoginActivity
import com.example.apiwithmvvm.activity.otp.OtpActivity
import com.example.apiwithmvvm.databinding.ActivitySignUpBinding
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.validation.Validation
import com.example.apiwithmvvm.validation.ValidationModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var binding: ActivitySignUpBinding
    private var activity = this@SignUpActivity
    private var isProfileSelected = false
    private var isPasswordVisible = false


    private var galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->

        if (isGranted == true) {

            galleryActivityResultLauncher.launch(Constants.image)

        }
    }

    private val galleryActivityResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // Handle the selected image URI
                // You can load the image into an ImageView or process it
                binding.ivProfile.setImageURI(uri)
                isProfileSelected = true

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(activity, R.layout.activity_sign_up)

        // Bind ViewModel to the layout
        binding.signUpViewModel = viewModel
        binding.lifecycleOwner = activity

        init()

    }

    private fun setObsOnValidationError() {

        viewModel.validationResult.observe(activity) { validationResult ->

            when (validationResult.tag) {

                Constants.profile -> {
                    Toast.makeText(activity, validationResult.errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }

                Constants.userName -> Utils.showError(
                    activity,
                    validationResult,
                    binding.etUserName,
                    binding.tvUserNameErr
                )

                Constants.name -> Utils.showError(
                    activity,
                    validationResult,
                    binding.etName,
                    binding.tvNameErr
                )

                Constants.email -> Utils.showError(
                    activity,
                    validationResult,
                    binding.etEmail,
                    binding.tvEmailErr
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

    private fun setObsOnSignUpResult() {

        val progressDialog = Utils.initProgressDialog(activity)

        viewModel.signUpResult.observe(activity) { result ->

            when (result) {

                is ApiResult.Loading -> {
                    progressDialog.show()
                }

                is ApiResult.Success -> {
                    // Handle success
                    progressDialog.dismiss()

                    if (result.statusCode == StatusCodeConstant.CREATED) {

                        val message = result.data?.message
                        val _id = result.data?._id
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

                        val bundle = Bundle()
                        bundle.putString(Constants.email,binding.etEmail.text.toString().lowercase().trim())
                        bundle.putString(Constants._id,_id)
                        Utils.I(activity, OtpActivity::class.java, bundle)

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

    private fun setTextChangeLis() {

        Utils.addTextChangedListener(binding.etUserName, binding.tvUserNameErr)

        Utils.addTextChangedListener(binding.etName, binding.tvNameErr)

        Utils.addTextChangedListener(binding.etEmail, binding.tvEmailErr)

        Utils.addTextChangedListener(binding.etPassword, binding.tvPasswordErr)

    }

    private fun setFocusChangeLis() {

        Utils.setFocusChangeListener(activity, binding.etUserName, binding.mcvUserName)

        Utils.setFocusChangeListener(activity, binding.etName, binding.mcvName)

        Utils.setFocusChangeListener(activity, binding.etEmail, binding.mcvEmail)

        Utils.setFocusChangeListener(activity, binding.etPassword, binding.mcvPassword)

    }

    private fun init() {

        setTextChangeLis()

        setFocusChangeLis()

        setObsOnValidationError()

        setObsOnSignUpResult()

        binding.mcvSignUp.setOnClickListener(activity)
        binding.tvLogin.setOnClickListener(activity)
        binding.tvSelectProfile.setOnClickListener(activity)
        binding.ivPasswordVisibility.setOnClickListener(activity)

    }

    override fun onClick(view: View?) {

        when (view) {

            binding.mcvSignUp -> {

                val validationModelList: MutableList<ValidationModel> = ArrayList()

                validationModelList.add(
                    ValidationModel(
                        Validation.Type.Profile,
                        isProfileSelected,
                        Constants.profile,
                    )
                )

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
                        binding.etName.text.toString().trim(),
                        Constants.name,
                    )
                )

                validationModelList.add(
                    ValidationModel(
                        Validation.Type.Email,
                        binding.etEmail.text.toString().trim(),
                        Constants.email,
                    )
                )

                validationModelList.add(
                    ValidationModel(
                        Validation.Type.Empty,
                        binding.etPassword.text.toString().trim(),
                        Constants.password,
                    )
                )

                val profileBitmap = binding.ivProfile.drawable.toBitmap()

                viewModel.validate(
                    profileBitmap,
                    validationModelList
                )

            }

            binding.tvLogin -> {

                Utils.I_clear(activity, LoginActivity::class.java, null)
            }

            binding.tvSelectProfile -> {

                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

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