package com.protose.resque
import com.redis.Redis
import FancySeq._
import java.util.Date
import com.twitter.json.Json

class Resque(val redis: Redis, val jobFactory: JobFactory) {
    def reserve(worker: Worker, name: String): Option[Job] = {
        try {
            val job = jobFactory(worker, name, pop(name))
            setWorkingOn(worker, job)
            Some(job)
        } catch {
            case e: NullPointerException => return None
        }
    }

    def failure(job: Job, exception: Throwable) = {
        val failure = Map("failed_at" -> new Date().toString,
                          "payload"   -> job.payload,
                          "error"     -> exception.getMessage,
                          "backtrace" -> exception.getStackTrace.map { s => s.toString },
                          "worker"    -> job.worker.id,
                          "queue"     -> job.queue)
        redis.pushTail("resque:failed", Json.build(failure).toString)
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
    protected def workerKey(worker: Worker) = {
        List("resque", worker.id).join(":")
    }

    protected def setWorkingOn(worker: Worker, job: Job) = {
        val data = Map("queue"   -> job.queue,
                       "run_at"  -> new Date().toString,
                       "payload" -> job.payload)
        redis.set(workerKey(worker), Json.build(data).toString)
    }
}

// vim: set ts=4 sw=4 et:
