package com.orbit.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo

class OrbitIME : InputMethodService() {
    private lateinit var kv: KeyboardView
    override fun onCreateInputView(): View { kv = KeyboardView(this); return kv }
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting); kv.reload()
    }
    fun commit(s: String) { currentInputConnection?.commitText(s, 1) }
    fun del() {
        val ic = currentInputConnection ?: return
        val sel = ic.getSelectedText(0)
        if (!sel.isNullOrEmpty()) ic.commitText("", 1) else ic.deleteSurroundingText(1, 0)
    }
    fun enter() {
        val ei = currentInputEditorInfo
        val a = ei.imeOptions and EditorInfo.IME_MASK_ACTION
        val noAction = ei.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION != 0
        if (!noAction && a != EditorInfo.IME_ACTION_NONE && a != EditorInfo.IME_ACTION_UNSPECIFIED)
            currentInputConnection?.performEditorAction(a) else commit("\n")
    }
}
