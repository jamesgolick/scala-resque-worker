package com.protose.resque
import com.redis.Redis
import Machine._
import com.protose.resque.FancySeq._
import java.util.Date

class Worker(redis: Redis, queues: List[String]) {
    def id = List(hostname, pid, queues.join(",")).join(":")

    def start = {
        addToWorkersSet
        started
    }

    protected def started         = redis.set(startedKey, new Date().toString)
    protected def startedKey      = List("worker", id, "started").join(":")
    protected def addToWorkersSet = redis.setAdd("workers", id)

}

// vim: set ts=4 sw=4 et:
