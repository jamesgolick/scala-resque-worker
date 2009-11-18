package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.protose.resque._
import com.protose.resque.Machine._
import com.protose.resque.FancySeq._
import com.redis.Redis
import java.util.Date

object WorkerSpec extends Specification with Mockito {
    val redis    = mock[Redis]
    val worker   = new Worker(redis, List("someAwesomeQueue", "someOtherAwesomeQueue"))
    val startKey = List("worker", worker.id, "started").join(":")

    "it has a string representation" in {
        val expectedId = hostname + ":" + pid + ":" + "someAwesomeQueue,someOtherAwesomeQueue"
        worker.id must_== expectedId
    }

    "starting a worker" in {
        val date     = new Date().toString
        redis.set(startKey, date) returns true
        redis.setAdd("workers", worker.id) returns true
        worker.start

        "adds the worker to the workers set" in {
            redis.setAdd("workers", worker.id) was called
        }

        "informs redis that work has started" in {
            redis.set(startKey, date) was called
        }
    }

    "stopping a worker" in {
        redis.delete(startKey) returns true
        redis.setDelete("workers", worker.id) returns true
        worker.stop

        "removes the worker from the workers set" in {
            redis.setDelete("workers", worker.id) was called
        }

        "deletes the started time" in {
            redis.delete(startKey) was called
        }
    }
}

// vim: set ts=4 sw=4 et:
