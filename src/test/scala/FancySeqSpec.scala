package test.scala
import org.specs.Specification
import com.protose.resque._
import com.protose.resque.FancySeq._

object FancySeqSpec extends Specification {
    "it joins seqs of strings together" in {
        List("1", "2", "3").join must_== "123"
    }
    "it joins with a delimiter" in {
        List("1", "2", "3").join(",") must_== "1,2,3"
    }
}

// vim: set ts=4 sw=4 et:
