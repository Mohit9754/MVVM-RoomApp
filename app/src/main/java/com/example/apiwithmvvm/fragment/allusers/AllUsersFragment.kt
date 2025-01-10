package com.example.apiwithmvvm.fragment.allusers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.UtilityTools.StatusCodeConstant
import com.example.apiwithmvvm.adapter.UserAdapter
import com.example.apiwithmvvm.databinding.FragmentAllUsersBinding
import com.example.apiwithmvvm.model.ApiResult
import com.example.apiwithmvvm.model.User
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import com.example.apiwithmvvm.R

@AndroidEntryPoint
class AllUsersFragment : Fragment() {

    private lateinit var binding: FragmentAllUsersBinding
    private val viewModel: AllUsersViewModel by viewModels()
    private var adapter: UserAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentAllUsersBinding.inflate(
            inflater,
            container,
            false
        )

        // Bind ViewModel to the layout
        binding.allUsersViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        init()

        return binding.root
    }



    private fun init() {

        setUserRecyclerView()
        setObsOnUserList()

    }

    override fun onResume() {
        super.onResume()
        // Don't call the API again unless you want to force a refresh
        viewModel.getAllUsers()
    }

    private fun setUserRecyclerView() {

        adapter = UserAdapter(requireActivity(), listOf())
        binding.rvUser.layoutManager = LinearLayoutManager(
            requireActivity(),
            RecyclerView.VERTICAL, false
        )
        binding.rvUser.adapter = adapter
    }

    private fun setObsOnUserList() {

        val progressDialog = Utils.initProgressDialog(requireActivity())

        viewModel.userList.observe(viewLifecycleOwner) { result ->

            when (result) {

                is ApiResult.Loading -> progressDialog.show()
                is ApiResult.Success -> {

                    if (progressDialog.isShowing) progressDialog.dismiss()

                    if (result.statusCode == StatusCodeConstant.OK) {
                        val userList = result.data
                        if (userList != null) {
                            adapter?.addList(userList)
                        }
                    }
                }

                is ApiResult.Error -> {

                    if (progressDialog.isShowing) progressDialog.dismiss()

                    handleApiError(result)
                }

                else -> {}
            }
        }
    }

    private fun handleApiError(result: ApiResult.Error<List<User>>) {

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

}

