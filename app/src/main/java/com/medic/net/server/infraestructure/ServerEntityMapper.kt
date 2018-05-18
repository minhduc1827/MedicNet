package com.medic.net.server.infraestructure

import com.medic.net.server.domain.model.Server
import com.medic.net.util.DataToDomain

class ServerEntityMapper : DataToDomain<ServerEntity, Server> {
    override fun translate(data: ServerEntity): Server {
        return Server(data.id, data.name, data.host, data.avatar)
    }
}
