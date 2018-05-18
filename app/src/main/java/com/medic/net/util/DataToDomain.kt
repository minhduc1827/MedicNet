package com.medic.net.util

interface DataToDomain<Data, Domain> {
    fun translate(data: Data): Domain
}
