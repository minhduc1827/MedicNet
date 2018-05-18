package com.medicnet.android.util

interface DomainToViewModel<Domain, ViewModel> {
    fun translate(domain: Domain): ViewModel
}
