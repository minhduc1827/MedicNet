package com.medic.net.util.extensions


inline fun CharSequence?.isNotNullNorEmpty(block: (CharSequence) -> Unit) {
    if (this != null && this.isNotEmpty()) {
        block(this)
    }
}