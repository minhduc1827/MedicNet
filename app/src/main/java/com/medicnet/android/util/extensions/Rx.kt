package com.medicnet.android.util.extensions

import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

fun EditText.asObservable(): Observable<CharSequence> {
    return RxTextView.textChanges(this)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(AndroidSchedulers.mainThread())
}