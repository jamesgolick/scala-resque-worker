package com.protose.resque
import Machine._
import com.protose.resque.FancySeq._
import java.util.Date
import com.redis.Redis
import net.lag.configgy.Configgy
import net.lag.logging.Logger
import java.io.File

object Runner {
    var redisHost = "localhost"
    var redisPort = 6379
    var queue     = ""
    val logger    = Logger.get

    def main(args: Array[String]) = {
        configure

        val redis  = new Redis(redisHost, redisPort)
        val resque = new Resque(redis, Job)
        val worker = new Worker(resque, List(queue))

        worker.workOff
    }

    protected def configure = {
        val envVar   = System.getenv.get("CONFIG")
        val filename = if (envVar == null) "/etc/resque.conf"
                       else envVar
        var file     = new File(filename)

        if (file.exists) {
            logger.info("Found configuration file at " + filename)
            configureFromFile(filename)
        } else { logger.info("No configuration file found. Using defaults.") }
        if (queue == "") attemptToGetQueueFromEnv

        logger.info("Listening on " + queue)
    }

    protected def configureFromFile(filename: String) = {
        Configgy.configure(filename)
        var config = Configgy.config
        redisHost  = config.getString("redis.host", "localhost")
        redisPort  = config.getInt("redis.port", 6379)
        queue      = config.getString("queue", "")
    }

    protected def attemptToGetQueueFromEnv = {
        queue = System.getenv.get("QUEUE")
        if (queue == null) {
            logger.critical("Couldn't find any queues to listen on. Exiting...")
            System.exit(1)
        }
    }
}

class Worker(resque: Resque, queues: List[String], sleepTime: Int) {
    var exit   = false
    val logger = Logger.get

    def this(resque: Resque, queues: List[String]) = this(resque, queues, 5000)

    def id = List(hostname, pid, queues.join(",")).join(":")

    def start = {
        resque.register(this)
        logger.info("Started worker " + id)
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
            resque.success(job)
        } catch {
            case exception: Throwable => failure(job, exception)
        }
    }

    protected def runInfiniteJobLoop: Unit = {
        while(true) {
            if (exit) { return }
            val job = nextJob
            if (job.isEmpty) {
                logger.debug("No jobs found. Sleeping for " + sleepTime + "ms.")
                Thread.sleep(sleepTime)
            } else {
                work(job.get)
            }
        }
    }

    protected def nextJob = {
        resque.reserve(this, queues.first)
    }

    protected def catchShutdown = {
        val thread = Thread.currentThread
        Runtime.getRuntime.addShutdownHook(new Thread() {
            override def run = {
                exit = true
                thread.join
            }
        })
    }

    protected def failure(job: Job, exception: Throwable) = {
        logger.debug(exception, "Failed to process job with exception.")
        resque.failure(job, exception)
    }
}

// vim: set ts=4 sw=4 et:
