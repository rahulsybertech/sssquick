package com.ssspvtltd.quick.utils.extension

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Abhishek Singh on February 05,2024.
 */
fun EditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            this.clearFocus()
            return@setOnEditorActionListener false
        }
        false
    }
}

fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        awaitClose { removeTextChangedListener(listener) }
    }
}