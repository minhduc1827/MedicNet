package com.medicnet.android.app.migration

import com.medicnet.android.app.migration.model.RealmBasedServerInfo
import io.realm.annotations.RealmModule

@RealmModule(library = true, classes = arrayOf(RealmBasedServerInfo::class))
class RocketChatServerModule