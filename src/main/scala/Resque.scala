package com.protose.resque
import com.redis.Redis
import FancySeq._
import java.util.Date

class Resque(val redis: Redis, val jobFactory: JobFactory) {
    def reserve(worker: Worker, name: String): Option[Job] = {
        try {
            Some(jobFactory(worker, name, pop(name)))
        } catch {
            case e: NullPointerException => return None
        }
    }

    def register(worker: Worker): Unit = {
        addToWorkersSet(worker)
        setStartedTime(worker)
    }

    def unregister(worker: Worker): Unit = {
        removeFromWorkersSet(worker)
        deleteStartTime(worker)
    }

    protected def pop(name: String): String = {
        redis.popHead(queueName(name))
    }

    protected def queueName(name: String) = List("resque", "queue", name).join(":")

    protected def setStartedTime(worker: Worker) = {
        redis.set(startedKey(worker), new Date().toString)
    }

    protected def startedKey(worker: Worker) = {
        List("resque:worker", worker.id, "started").join(":")
    }

    protected def addToWorkersSet(worker: Worker) = {
        redis.setAdd(workerSet, worker.id)
    }

    protected def removeFromWorkersSet(worker: Worker) = {
        redis.setDelete(workerSet, worker.id)
    }

    protected def deleteStartTime(worker: Worker) = {
        redis.delete(startedKey(worker))
    }

    protected def workerSet = "resque:workers"
}

// vim: set ts=4 sw=4 et:
