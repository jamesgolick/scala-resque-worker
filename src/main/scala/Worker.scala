package com.protose.resque
import Machine._
import com.protose.resque.FancySeq._
import java.util.Date

class Worker(resque: Resque, queues: List[String]) {
    def id = List(hostname, pid, queues.join(",")).join(":")

    def start = {
        resque.register(this)
    }

    def stop = {
        resque.unregister(this)
    }

    def workNextJob = {
        val job = nextJob.get
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
