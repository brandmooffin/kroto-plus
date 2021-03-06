package com.github.marcoferrer.krotoplus.utils

import com.github.marcoferrer.krotoplus.utils.CommonPackages.kotlinxCoroutines
import com.github.marcoferrer.krotoplus.utils.CommonPackages.krotoCoroutineLib
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import io.grpc.BindableService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


object CommonClassNames{

    val bindableService: ClassName = BindableService::class.asClassName()
    val coroutineScope: ClassName = CoroutineScope::class.asClassName()
    val coroutineContext: ClassName = CoroutineContext::class.asClassName()
    val emptyCoroutineContext: ClassName = EmptyCoroutineContext::class.asClassName()
    val receiveChannel: ClassName = ReceiveChannel::class.asClassName()
    val sendChannel: ClassName = SendChannel::class.asClassName()
    val dispatchers: ClassName = Dispatchers::class.asClassName()
    val completableDeferred: ClassName = CompletableDeferred::class.asClassName()
    val streamObserver: ClassName = ClassName("io.grpc.stub", "StreamObserver")
    val serverCalls = ClassName(krotoCoroutineLib,"ServerCalls")
    val launch: ClassName = ClassName(kotlinxCoroutines,"launch")
    val grpcChannel: ClassName = ClassName("io.grpc","Channel")
    val grpcCallOptions: ClassName = ClassName("io.grpc","CallOptions")
    val grpcContextElement: ClassName = ClassName(krotoCoroutineLib,"GrpcContextElement")
    val obsoleteCoroutinesApi: ClassName = ClassName(kotlinxCoroutines, "ObsoleteCoroutinesApi")
    val experimentalCoroutinesApi: ClassName = ClassName(kotlinxCoroutines, "ExperimentalCoroutinesApi")

    object ClientCalls {
        val clientCallUnary: ClassName = ClassName("$krotoCoroutineLib.client", "clientCallUnary")
        val clientCallServerStreaming: ClassName = ClassName("$krotoCoroutineLib.client", "clientCallServerStreaming")
        val clientCallBidiStreaming: ClassName = ClassName("$krotoCoroutineLib.client", "clientCallBidiStreaming")
        val clientCallClientStreaming: ClassName = ClassName("$krotoCoroutineLib.client", "clientCallClientStreaming")
    }

    object ClientChannels {
        val clientBidiCallChannel: ClassName = ClassName("$krotoCoroutineLib.client", "ClientBidiCallChannel")
        val clientStreamingCallChannel: ClassName = ClassName("$krotoCoroutineLib.client", "ClientStreamingCallChannel")
    }

    object ServerCalls {

        val serverCallUnary = ClassName("$krotoCoroutineLib.server","serverCallUnary")
        val serverCallClientStreaming = ClassName("$krotoCoroutineLib.server","serverCallClientStreaming")
        val serverCallServerStreaming = ClassName("$krotoCoroutineLib.server","serverCallServerStreaming")
        val serverCallBidiStreaming = ClassName("$krotoCoroutineLib.server","serverCallBidiStreaming")
        val serverCallUnimplementedUnary = ClassName("$krotoCoroutineLib.server","serverCallUnimplementedUnary")
        val serverCallUnimplementedStream = ClassName("$krotoCoroutineLib.server","serverCallUnimplementedStream")
    }
}

object CommonPackages {

    const val krotoCoroutineLib = "com.github.marcoferrer.krotoplus.coroutines"
    const val kotlinxCoroutines = "kotlinx.coroutines"
}