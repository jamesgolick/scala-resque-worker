package test.scala
import org.specs.Specification
import com.protose.resque._

object PerformableSpec extends Specification {
    object someBackgroundJob extends Performable("SomeBackgroundJob") {
        def perform(args: Map[String, String]) = {}
    }

    "has a convenience method for getting performables" in {
        Performable("SomeBackgroundJob") must_== someBackgroundJob
    }
}


// vim: set ts=4 sw=4 et:
