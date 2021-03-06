package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.protose.resque._
import com.redis.Redis
import FancySeq._
import java.util.Date
import java.lang.NullPointerException
import com.twitter.json.Json

object ResqueSpec extends Specification with Mockito {
    val redis      = mock[Redis]
    val jobFactory = mock[JobFactory]
    val resque     = new Resque(redis, jobFactory)
    val worker     = new Worker(resque, List("some queue"))
    val job        = Job(worker, "some_queue", "{}", Map[String, Performable]())
    val startKey   = List("resque", "worker", worker.id, "started").join(":")

    "reserving a job" in {
        "when there is a job" in {
            redis.popHead("resque:queue:some_queue") returns "{}"
            jobFactory.apply(worker, "some_queue", "{}") returns job
            val returnVal = resque.reserve(worker, "some_queue").get

            "fetches the next payload from the queue" in {
                redis.popHead("resque:queue:some_queue") was called
            }

            "returns the job created with {}" in {
                jobFactory.apply(worker, "some_queue", "{}") was called
                returnVal must_== job
            }

            "tells redis about the job we're processing" in {
                val json = Map("queue"   -> "some_queue",
                               "run_at"  -> new Date().toString,
                               "payload" -> Map[String, String]())
                redis.set("resque:worker:" + worker.id, Json.build(json).toString) was called
            }
        }

        "when there is no job" in {
            redis.popHead("resque:queue:some_queue") throws new NullPointerException
            val returnVal = resque.reserve(worker, "some_queue")

            "returns None" in {
                returnVal must_== None
            }
        }
    }

    "registering a worker" in {
        val date     = new Date().toString
        redis.setAdd("resque:workers", worker.id) returns true
        resque.register(worker)

        "adds the worker to the workers set" in {
            redis.setAdd("resque:workers", worker.id) was called
        }

        "informs redis that work has started" in {
            redis.set(startKey, date) was called
        }
    }

    "stopping a worker" in {
        redis.delete(startKey) returns true
        redis.setDelete("resque:workers", worker.id) returns true
        resque.unregister(worker)

        "removes the worker from the workers set" in {
            redis.setDelete("resque:workers", worker.id) was called
        }

        "deletes the started time" in {
            redis.delete(startKey) was called
        }
    }

    "registering a failure" in {
        val exception = new NullPointerException("AHHHH!!!!")
        val trace     = exception.getStackTrace.map { s => s.toString}
        val payload   = job.parsedPayload
        val failure = Map("failed_at" -> new Date().toString,
                          "payload"   -> payload,
                          "error"     -> exception.getMessage,
                          "backtrace" -> trace,
                          "worker"    -> job.worker.id,
                          "queue"     -> job.queue)
        val json      = Json.build(failure).toString
        redis.pushTail("resque:failed", json) returns true
        resque.failure(job, exception)
        
        "jsonifies the data and pushes it on to the failures queue" in {
            redis.pushTail("resque:failed", json) was called
        }

        "increments the failures for this worker" in {
            redis.incr("resque:stat:failed:" + worker.id) was called
        }

        "increments the overall failure stats" in {
            redis.incr("resque:stat:failed") was called
        }

        "tells redis we're not workign on the job anymore" in {
            redis.delete("resque:worker:" + worker.id) was called
        }
    }

    "registering success" in {
        resque.success(job)

        "increments the processed for this worker" in {
            redis.incr("resque:stat:processed:" + worker.id) was called
        }

        "increments the overall processed stats" in {
            redis.incr("resque:stat:processed") was called
        }

        "tells redis we're not workign on the job anymore" in {
            redis.delete("resque:worker:" + worker.id) was called
        }
    }
}

// vim: set ts=4 sw=4 et:
