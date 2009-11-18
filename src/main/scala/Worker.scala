package com.protose.resque
import com.redis.Redis
import Machine._
import com.protose.resque.FancySeq._

class Worker(redis: Redis, queues: List[String]) {
    def id = List(hostname, pid, queues.join(",")).join(":")
}

// vim: set ts=4 sw=4 et:
