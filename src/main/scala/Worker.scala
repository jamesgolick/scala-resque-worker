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
}

// vim: set ts=4 sw=4 et:
