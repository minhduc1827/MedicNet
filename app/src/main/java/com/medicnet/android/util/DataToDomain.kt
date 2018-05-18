package com.medicnet.android.util

interface DataToDomain<Data, Domain> {
    fun translate(data: Data): Domain
}
