package com.medicnet.android.util.extensions


inline fun CharSequence?.isNotNullNorEmpty(block: (CharSequence) -> Unit) {
    if (this != null && this.isNotEmpty()) {
        block(this)
    }
}