package com.protose.resque
import com.redis.Redis
import FancySeq._

class Queue(val redis: Redis, val jobFactory: JobFactory) {
    def reserve(worker: Worker, name: String): Job = {
        jobFactory(worker, name, pop(name))
    }

    protected def pop(name: String): String = {
        redis.popHead(queueName(name))
    }

    protected def queueName(name: String) = List("resque", "queue", name).join(":")
}

// vim: set ts=4 sw=4 et:
