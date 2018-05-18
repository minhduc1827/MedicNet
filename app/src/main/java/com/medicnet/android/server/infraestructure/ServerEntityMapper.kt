package com.medicnet.android.server.infraestructure

import com.medicnet.android.server.domain.model.Server
import com.medicnet.android.util.DataToDomain

class ServerEntityMapper : DataToDomain<ServerEntity, Server> {
    override fun translate(data: ServerEntity): Server {
        return Server(data.id, data.name, data.host, data.avatar)
    }
}
