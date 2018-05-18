package com.medic.net.dagger.module

import com.medic.net.push.DeleteReceiver
import com.medic.net.push.DirectReplyReceiver
import com.medic.net.push.DirectReplyReceiverProvider
import com.medic.net.push.di.DeleteReceiverProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReceiverBuilder {

    @ContributesAndroidInjector(modules = [DeleteReceiverProvider::class])
    abstract fun bindDeleteReceiver(): DeleteReceiver

    @ContributesAndroidInjector(modules = [DirectReplyReceiverProvider::class])
    abstract fun bindDirectReplyReceiver(): DirectReplyReceiver
}