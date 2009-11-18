package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.protose.resque._
import com.protose.resque.Machine._
import com.redis.Redis

object WorkerSpec extends Specification with Mockito {
    val redis  = mock[Redis]
    val worker = new Worker(redis, List("someAwesomeQueue", "someOtherAwesomeQueue"))

    "it has a string representation" in {
        val expectedId = hostname + ":" + pid + ":" + "someAwesomeQueue,someOtherAwesomeQueue"
        worker.id must_== expectedId
    }
}

// vim: set ts=4 sw=4 et:
