package com.protose.resque
import Machine._
import com.protose.resque.FancySeq._
import java.util.Date

class Worker(resque: Resque, queues: List[String], sleepTime: Int) {
    def this(resque: Resque, queues: List[String]) = this(resque, queues, 5000)

    def id = List(hostname, pid, queues.join(",")).join(":")

    def start = {
        resque.register(this)
    }

    def stop = {
        resque.unregister(this)
    }

    def workOff = {
        start
        while(true) {
            val job = nextJob
            if (job.isEmpty) {
                Thread.sleep(sleepTime)
            } else {
                work(job.get)
            }
        }
        stop
    }

    def work(job: Job) = {
        try {
            job.perform
        } catch {
            case exception: Throwable => resque.failure(job, exception)
        }
    }

    protected def nextJob = {
        resque.reserve(this, queues.first)
    }
}

// vim: set ts=4 sw=4 et:
