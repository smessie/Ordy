package com.ordy.app.util

import com.google.android.material.textfield.TextInputLayout

class InputUtil {
    companion object {

        /**
         * Extract the text from a text input.
         */
        fun extractText(textInput: TextInputLayout): String {
            return textInput.editText?.text.toString()
        }
    }
}