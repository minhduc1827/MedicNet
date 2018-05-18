package com.medic.net.app.migration

import com.medic.net.app.migration.model.RealmBasedServerInfo
import io.realm.annotations.RealmModule

@RealmModule(library = true, classes = arrayOf(RealmBasedServerInfo::class))
class RocketChatServerModule