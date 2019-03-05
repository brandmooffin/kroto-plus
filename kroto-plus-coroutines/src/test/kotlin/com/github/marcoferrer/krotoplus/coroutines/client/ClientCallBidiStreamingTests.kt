/*
 * Copyright 2019 Kroto+ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.marcoferrer.krotoplus.coroutines.client


import com.github.marcoferrer.krotoplus.coroutines.utils.assertCancellationError
import com.github.marcoferrer.krotoplus.coroutines.utils.assertFailsWithStatusCode
import com.github.marcoferrer.krotoplus.coroutines.withCoroutineContext
import io.grpc.*
import io.grpc.examples.helloworld.GreeterGrpc
import io.grpc.examples.helloworld.HelloReply
import io.grpc.examples.helloworld.HelloRequest
import io.grpc.stub.StreamObserver
import io.grpc.testing.GrpcServerRule
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.junit.Rule
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class ClientCallBidiStreamingTests {

    @[Rule JvmField]
    var grpcServerRule = GrpcServerRule().directExecutor()

    private val methodDescriptor = GreeterGrpc.getSayHelloStreamingMethod()
    private val service = spyk(object : GreeterGrpc.GreeterImplBase() {})

    inner class RpcSpy{
        val stub: GreeterGrpc.GreeterStub
        lateinit var call: ClientCall<HelloRequest,HelloReply>

        init {
            val channelSpy = spyk(grpcServerRule.channel)
            stub = GreeterGrpc.newStub(channelSpy)

            every { channelSpy.newCall(methodDescriptor, any()) } answers {
                spyk(grpcServerRule.channel.newCall(methodDescriptor, secondArg<CallOptions>())).also {
                    this@RpcSpy.call = it
                }
            }
        }
    }

    private fun setupServerHandlerError(){
        every { service.sayHelloStreaming(any()) } answers {
            val responseObserver = firstArg<StreamObserver<HelloReply>>()
            object : StreamObserver<HelloRequest>{
                var reqQty = 0
                override fun onNext(value: HelloRequest) {
                    if(reqQty == 3){
                        responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException())
                    }else{
                        responseObserver.onNext(HelloReply.newBuilder()
                            .setMessage("Req:#${value.name}/Resp:#${reqQty++}")
                            .build())
                    }
                }
                override fun onError(t: Throwable?) {}
                override fun onCompleted() {
                    responseObserver.onCompleted()
                }
            }
        }
    }

    private fun setupServerHandlerSuccess(){
        every { service.sayHelloStreaming(any()) } answers {
            val responseObserver = firstArg<StreamObserver<HelloReply>>()
            object : StreamObserver<HelloRequest>{
                var reqQty = 0
                override fun onNext(value: HelloRequest) {
                    responseObserver.onNext(HelloReply.newBuilder()
                        .setMessage("Req:#${value.name}/Resp:#${reqQty++}")
                        .build())
                }
                override fun onError(t: Throwable?) {}
                override fun onCompleted() {
                    responseObserver.onCompleted()
                }
            }
        }
    }

    private fun setupServerHandlerNoop(){
        every { service.sayHelloStreaming(any()) } answers {
            object : StreamObserver<HelloRequest>{
                override fun onNext(value: HelloRequest) {}
                override fun onError(t: Throwable?) {}
                override fun onCompleted() {}
            }
        }
    }

    @BeforeTest
    fun setupService() {
        grpcServerRule.serviceRegistry.addService(service)
    }

    @Test
    fun `Call succeeds on server response`() {
        val rpcSpy = RpcSpy()
        val stub = rpcSpy.stub

        setupServerHandlerSuccess()
        val (requestChannel, responseChannel) = stub
            .clientCallBidiStreaming(methodDescriptor)

        runBlocking(Dispatchers.Default) {
            launch {
                repeat(3){
                    requestChannel.send(
                        HelloRequest.newBuilder()
                            .setName(it.toString())
                            .build())
                }
                requestChannel.close()
            }
            launch{
                repeat(3){
                    assertEquals("Req:#$it/Resp:#$it",responseChannel.receive().message)
                }
            }
        }

        verify(exactly = 0) { rpcSpy.call.cancel(any(), any()) }
        assert(requestChannel.isClosedForSend) { "Request channel should be closed for send" }
        assert(responseChannel.isClosedForReceive) { "Response channel should be closed for receive" }
    }


}