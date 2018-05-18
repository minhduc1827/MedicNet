package com.medicnet.android.server.domain

import com.medicnet.android.authentication.domain.model.TokenModel

interface MultiServerTokenRepository {
    fun get(server: String): TokenModel?

    fun save(server: String, token: TokenModel)

    fun clear(server: String)
}