package com.medic.net.util

interface DomainToViewModel<Domain, ViewModel> {
    fun translate(domain: Domain): ViewModel
}
