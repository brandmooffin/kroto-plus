package com.github.marcoferrer.krotoplus.proto

import com.google.protobuf.DescriptorProtos
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import io.grpc.MethodDescriptor

class ProtoMethod(
    override val descriptorProto: DescriptorProtos.MethodDescriptorProto,
    val protoService: ProtoService
) : Schema.DescriptorWrapper {

    val functionName = descriptorProto.name.decapitalize()

    val methodDefinitionGetterName = "get${descriptorProto.name}Method"

    val requestType = protoService.protoFile.schema.protoTypes[descriptorProto.inputType]
        ?: throw IllegalStateException("${descriptorProto.inputType} was not found in schema type map.")

    val requestClassName = requestType.className

    val responseType = protoService.protoFile.schema.protoTypes[descriptorProto.outputType]
        ?: throw IllegalStateException("${descriptorProto.inputType} was not found in schema type map.")

    val responseClassName = responseType.className

    val isEmptyInput get() = descriptorProto.inputType == ".google.protobuf.Empty"

    val isNotEmptyInput get() = !isEmptyInput

    val isUnary get() = !descriptorProto.clientStreaming && !descriptorProto.serverStreaming

    val isBidi get() = descriptorProto.clientStreaming && descriptorProto.serverStreaming

    val isServerStream get() = !descriptorProto.clientStreaming && descriptorProto.serverStreaming

    val isClientStream get() = descriptorProto.clientStreaming && !descriptorProto.serverStreaming

    val type: MethodDescriptor.MethodType
        get() = when{
            isUnary -> MethodDescriptor.MethodType.UNARY
            isBidi ->  MethodDescriptor.MethodType.BIDI_STREAMING
            isServerStream ->  MethodDescriptor.MethodType.SERVER_STREAMING
            isClientStream ->  MethodDescriptor.MethodType.CLIENT_STREAMING
            else -> throw IllegalStateException("Unknown method type")
        }
}