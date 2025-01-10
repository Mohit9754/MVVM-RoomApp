package com.example.apiwithmvvm.fragment.profile

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.apitesting.basic.UtilityTools.Constants.Companion.email
import com.example.apitesting.basic.UtilityTools.Constants.Companion.image
import com.example.apitesting.basic.UtilityTools.Constants.Companion.userName
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apitesting.model.UpdateResponse
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.UtilityTools.StatusCodeConstant
import com.example.apiwithmvvm.database.User
import com.example.apiwithmvvm.databinding.FragmentProfileBinding
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.validation.Validation
import com.example.apiwithmvvm.validation.ValidationModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val validationModelList: MutableList<ValidationModel> = ArrayList()

    private var galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->

        if (isGranted == true) {

            galleryActivityResultLauncher.launch(image)

        }
    }

    private val galleryActivityResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            uri?.let {

                // Handle the selected image URI
                // You can load the image into an ImageView or process it
                binding.ivProfile.setImageURI(uri)

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )  // Use inflater to inflate the view

        // Bind ViewModel to the layout
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        init()

        return binding.root
    }

    private fun setObsOnValidationError() {

        viewModel.validationResult.observe(viewLifecycleOwner) { validationResult ->

            when (validationResult.tag) {

                userName -> Utils.showError(
                    requireActivity(),
                    validationResult,
                    binding.etName,
                    binding.tvNameErr
                )

                email -> Utils.showError(
                    requireActivity(),
                    validationResult,
                    binding.etEmail,
                    binding.tvEmailErr
                )

                else -> {}
            }

        }
    }

    private fun setObsOnUpdateResult() {

        val progressDialog = Utils.initProgressDialog(activity)

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->

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

                            viewModel.updateUser(

                                User(
                                    name = user.name,
                                    email = user.email,
                                    profileImageUrl = user.profileImageUrl,
                                )

                            )
                        }

                        Toast.makeText(activity, "${result.data?.message}", Toast.LENGTH_SHORT)
                            .show()

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

    private fun handleApiError(result: ApiResult.Error<UpdateResponse>) {

        when (result.statusCode) {

            StatusCodeConstant.BAD_REQUEST -> {

                Toast.makeText(activity, "${result.message}", Toast.LENGTH_SHORT).show()
            }

            StatusCodeConstant.UNAUTHORIZED -> {

                viewModel.deleteAllUser()

                Utils.UnAuthorizationToken(requireActivity())

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

        Utils.addTextChangedListener(binding.etName, binding.tvNameErr)

        Utils.addTextChangedListener(binding.etEmail, binding.tvEmailErr)

    }

    private fun setFocusChangeLis() {

        Utils.setFocusChangeListener(requireActivity(), binding.etName, binding.mcvName)

        Utils.setFocusChangeListener(requireActivity(), binding.etEmail, binding.mcvEmail)

    }

    private fun init() {

        setTextChangeLis()

        setFocusChangeLis()

        setObsOnValidationError()

        setObsOnUpdateResult()

        binding.tvSelectProfile.setOnClickListener(this)

        binding.mcvSignUp.setOnClickListener(this)

    }

    override fun onClick(view: View?) {

        when (view) {

            binding.tvSelectProfile -> {

                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            }

            binding.mcvSignUp -> {

                validationModelList.clear()

                validationModelList.add(
                    ValidationModel(
                        Validation.Type.Empty,
                        binding.etName.text.toString(),
                        userName,
                    )
                )

                validationModelList.add(
                    ValidationModel(
                        Validation.Type.Email,
                        binding.etEmail.text.toString(),
                        email,
                    )
                )

                val profileBitmap = binding.ivProfile.drawable.toBitmap()

                viewModel.validate(
                    profileBitmap,
                    validationModelList
                )

            }

        }

    }

}