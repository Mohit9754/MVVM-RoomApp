package com.example.apiwithmvvm.validation

import android.content.Context
import com.example.apiwithmvvm.R
import javax.inject.Inject

class Validation @Inject constructor(val context: Context) {

    private var tag: String? = null
    private var errorMessage: String? = null

    fun CheckValidation(
        errorValidationModels: List<ValidationModel>
    ): ValidationResult {

        var validationCheck = false

        for (validationModel in errorValidationModels) {

            tag = validationModel.tag

            when (validationModel.type) {

                Type.Email -> validationCheck = isEmailValid(validationModel.data)

                Type.Empty -> validationCheck = isEmpty(validationModel.data)

                Type.Profile -> validationCheck = isProfileSelected(validationModel.isProfileSelected)

                else -> {}
            }
            if (!validationCheck) {
                break
            }
        }

        return ValidationResult(validationCheck, errorMessage,tag)

    }

    private fun isProfileSelected(profileSelected: Boolean?): Boolean {

        if (profileSelected == null) false

        return if (!profileSelected!!) {

            errorMessage = context.getString(R.string.select_profile_picture)

            false
        }
        else {
            true
        }

    }

    private fun isEmailValid(email: String?): Boolean {

        return if (email?.trim()?.isEmpty() == true) {
            errorMessage = context.getString(R.string.empty_error)
            false
        } else {

            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"

            if (email?.matches(emailRegex.toRegex()) == true) {
                true
            } else {
                errorMessage = context.getString(R.string.invalid_email)
                false
            }
        }
    }

    private fun isEmpty(data: String?): Boolean {

        return if (data?.trim()?.isEmpty() == true) {
            errorMessage = context.getString(R.string.empty_error)
            false
        } else {
            true
        }
    }

    /**
     * Enum of the Type of error we have
     */
    enum class Type(var label: String) {

        Empty(""), Email(""), Profile("")

    }

}