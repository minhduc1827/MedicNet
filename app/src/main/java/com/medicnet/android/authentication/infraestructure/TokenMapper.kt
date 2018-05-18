package com.medicnet.android.authentication.infraestructure

import chat.rocket.common.model.Token
import com.medicnet.android.authentication.domain.model.TokenModel
import com.medicnet.android.util.DataToDomain

object TokenMapper : DataToDomain<Token, TokenModel> {
    override fun translate(data: Token): TokenModel {
        return TokenModel(data.userId, data.authToken)
    }
}