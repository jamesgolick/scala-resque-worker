package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import org.mockito.Matchers._
import com.protose.resque._
import com.protose.resque.Machine._
import com.protose.resque.FancySeq._
import com.redis.Redis
import java.util.Date

object WorkerSpec extends Specification with Mockito {
    val resque   = mock[Resque]
    val worker   = new Worker(resque, List("someAwesomeQueue", "someOtherAwesomeQueue"))
    val startKey = List("resque", "worker", worker.id, "started").join(":")
    val job      = mock[Job]

    "it has a string representation" in {
        val expectedId = hostname + ":" + pid + ":" + "someAwesomeQueue,someOtherAwesomeQueue"
        worker.id must_== expectedId
    }

    "starting a worker" in {
        worker.start

        "registers the worker with resque" in {
            resque.register(worker) was called
        }
    }

    "stopping a worker" in {
        worker.stop

        "unregisters the worker" in {
            resque.unregister(worker) was called
        }
    }

    "working off the next job" in {
        "when the job succeeds" in {
            worker.work(job)

            "performs the job" in {
                job.perform was called
            }
        }

        "when the job fails" in {
            val exception = new NullPointerException("asdf")
            job.perform throws exception
            worker.work(job)

            "it registers a failure" in {
                resque.failure(job, exception) was called
            }
        }
    }
}

// vim: set ts=4 sw=4 et:
