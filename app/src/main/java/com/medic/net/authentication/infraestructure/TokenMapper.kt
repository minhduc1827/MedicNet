package com.medic.net.authentication.infraestructure

import com.medic.net.authentication.domain.model.TokenModel
import com.medic.net.util.DataToDomain
import chat.rocket.common.model.Token

object TokenMapper : DataToDomain<Token, TokenModel> {
    override fun translate(data: Token): TokenModel {
        return TokenModel(data.userId, data.authToken)
    }
}