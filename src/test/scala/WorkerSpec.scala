package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.protose.resque._
import com.protose.resque.Machine._
import com.protose.resque.FancySeq._
import com.redis.Redis
import java.util.Date

object WorkerSpec extends Specification with Mockito {
    val resque   = mock[Resque]
    val worker   = new Worker(resque, List("someAwesomeQueue", "someOtherAwesomeQueue"))
    val startKey = List("resque", "worker", worker.id, "started").join(":")

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
}

// vim: set ts=4 sw=4 et:
