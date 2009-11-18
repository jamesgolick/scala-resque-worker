package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.protose.resque._
import com.redis.Redis

object ResqueSpec extends Specification with Mockito {
    val redis      = mock[Redis]
    val jobFactory = mock[JobFactory]
    val worker     = mock[Worker]
    val job        = Job(worker, "some_queue", "the payload")
    val queue      = new Resque(redis, jobFactory)

    "reserving a job" in {
        redis.popHead("resque:queue:some_queue") returns "the payload"
        jobFactory.apply(worker, "some_queue", "the payload") returns job
        val returnVal = queue.reserve(worker, "some_queue")

        "fetches the next payload from the queue" in {
            redis.popHead("resque:queue:some_queue") was called
        }

        "returns the job created with the payload" in {
            jobFactory.apply(worker, "some_queue", "the payload") was called
            returnVal must_== job
        }
    }
}

// vim: set ts=4 sw=4 et:
