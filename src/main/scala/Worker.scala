package com.protose.resque
import Machine._
import com.protose.resque.FancySeq._
import java.util.Date
import com.redis.Redis

object Runner {
    def main(args: Array[String]) = {
        val redis  = new Redis
        val resque = new Resque(redis, Job)
        val worker = new Worker(resque, List("activity_feed"))
        worker.workOff
    }
}

class Worker(resque: Resque, queues: List[String], sleepTime: Int) {
    var exit = false

    def this(resque: Resque, queues: List[String]) = this(resque, queues, 5000)

    def id = List(hostname, pid, queues.join(",")).join(":")

    def start = {
        resque.register(this)
    }

    def stop = {
        resque.unregister(this)
    }

    def workOff = {
        catchShutdown
        start
        runInfiniteJobLoop
        stop
    }

    def work(job: Job) = {
        try {
            job.perform
        } catch {
            case exception: Throwable => resque.failure(job, exception)
        }
    }

    protected def runInfiniteJobLoop: Unit = {
        while(true) {
            val job = nextJob
            if (job.isEmpty) {
                Thread.sleep(sleepTime)
            } else {
                work(job.get)
            }
            if (exit) { return }
        }
    }

    protected def nextJob = {
        resque.reserve(this, queues.first)
    }

    protected def catchShutdown = {
        Runtime.getRuntime.addShutdownHook(new Thread() {
            override def run = exit = true
        })
    }
}

// vim: set ts=4 sw=4 et:
