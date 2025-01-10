package com.example.apiwithmvvm.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.UtilityTools.StatusCodeConstant
import com.example.apiwithmvvm.activity.login.LoginActivity
import com.example.apiwithmvvm.databinding.FragmentMoreBinding
import com.example.apiwithmvvm.model.ApiResponse
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentMoreBinding
    private val viewModel: MoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(
            inflater,
            container,
            false
        )  // Use inflater to inflate the view

        // Bind ViewModel to the layout
        binding.moreViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        init()

        return binding.root
    }

    private fun setObsOnDeleteAccountResult() {

        val progressDialog = Utils.initProgressDialog(activity)

        viewModel.deleteAccountResult.observe(viewLifecycleOwner) { result ->

            when (result) {

                is ApiResult.Loading -> {

                    progressDialog.show()
                }

                is ApiResult.Success -> {

                    // Handle success
                    progressDialog.dismiss()

                    if (result.statusCode == StatusCodeConstant.OK) {

                        viewModel.deleteUser()

                        Toast.makeText(
                            requireActivity(),
                            "${result.data?.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                        Utils.I_clear(requireActivity(), LoginActivity::class.java, null)

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

            else -> {

                Toast.makeText(
                    activity,
                    getString(R.string.unknown_error, result.statusCode.toString()),
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun init() {

        setObsOnDeleteAccountResult()

        binding.mcvLogOut.setOnClickListener(this)

    }

    override fun onClick(view: View?) {

        when (view) {

            binding.mcvLogOut -> {

                viewModel.deleteUser()

                Toast.makeText(activity, getString(R.string.logout_successfully), Toast.LENGTH_SHORT)
                    .show()

                Utils.I(requireActivity(), LoginActivity::class.java, null)

            }

        }
    }
}