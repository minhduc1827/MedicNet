package com.medicnet.android.dagger.module

import com.medicnet.android.push.DeleteReceiver
import com.medicnet.android.push.DirectReplyReceiver
import com.medicnet.android.push.DirectReplyReceiverProvider
import com.medicnet.android.push.di.DeleteReceiverProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReceiverBuilder {

    @ContributesAndroidInjector(modules = [DeleteReceiverProvider::class])
    abstract fun bindDeleteReceiver(): DeleteReceiver

    @ContributesAndroidInjector(modules = [DirectReplyReceiverProvider::class])
    abstract fun bindDirectReplyReceiver(): DirectReplyReceiver
}