import com.google.common.util.concurrent.UncaughtExceptionHandlers
import io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.Dispatchers

import java.util.concurrent.*
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory
import java.util.concurrent.ForkJoinPool.defaultForkJoinWorkerThreadFactory
import java.util.concurrent.atomic.AtomicInteger

// TODO: add args to enabled direct executor
fun main(){

//    ForkJoinPool.commonPool()
//    Dispatchers.Default
//    val fjpExecutor = ForkJoinPool(
//        4 /* parallelism*/, object : ForkJoinWorkerThreadFactory {
//
//        private val num = AtomicInteger()
//        override fun newThread(pool: ForkJoinPool): ForkJoinWorkerThread {
//            val thread = defaultForkJoinWorkerThreadFactory.newThread(pool)
//            thread.isDaemon = true
//            thread.name = "grpc-server-app-" + "-" + num.getAndIncrement()
//            return thread
//        }
//    }, UncaughtExceptionHandlers.systemExit(), true /* async */)

    val server = NettyServerBuilder
        .forPort(8000)
        .addService(BenchMarkService())
//        .executor(ForkJoinPool.commonPool())
        .directExecutor()
        .build()
        .apply { start() }

    server.awaitTermination()
}
