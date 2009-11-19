package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.twitter.json.Json
import com.protose.resque._

object JobSpec extends Specification with Mockito {
    val worker          = mock[Worker]
    val queue           = "some_awesome_queue"
    val payload         = Map("args" -> List("arg1"), "class" -> "SomeAwesomeJob")
    val performable     = new Performable { 
        def perform(args: List[String]) = {}
    }
    val job             = Job(worker, queue, Json.build(payload).toString, Map("SomeAwesomeJob" -> performable))

    "performing a job" in {
        job.perform

        "calls perform on the performable with the args" in {
            performable.perform(List("arg1")) was called
        }
    }
}

// vim: set ts=4 sw=4 et:
