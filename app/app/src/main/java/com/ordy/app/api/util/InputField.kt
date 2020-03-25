package com.ordy.app.api.util

import com.google.android.material.textfield.TextInputLayout

data class InputField(

    /**
     * Name of the field
     */
    val name: String,

    /**
     * Input object of the field
     */
    val input: TextInputLayout
)