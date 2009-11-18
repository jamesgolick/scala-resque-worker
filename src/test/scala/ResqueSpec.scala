package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.protose.resque._
import com.redis.Redis
import FancySeq._
import java.util.Date

object ResqueSpec extends Specification with Mockito {
    val redis      = mock[Redis]
    val jobFactory = mock[JobFactory]
    val worker     = new Worker(redis, List("some queue"))
    val job        = Job(worker, "some_queue", "the payload")
    val resque     = new Resque(redis, jobFactory)
    val startKey   = List("resque", "worker", worker.id, "started").join(":")

    "reserving a job" in {
        redis.popHead("resque:queue:some_queue") returns "the payload"
        jobFactory.apply(worker, "some_queue", "the payload") returns job
        val returnVal = resque.reserve(worker, "some_queue")

        "fetches the next payload from the queue" in {
            redis.popHead("resque:queue:some_queue") was called
        }

        "returns the job created with the payload" in {
            jobFactory.apply(worker, "some_queue", "the payload") was called
            returnVal must_== job
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
}

// vim: set ts=4 sw=4 et:
