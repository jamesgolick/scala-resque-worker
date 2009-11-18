package test.scala
import org.specs.Specification
import org.specs.mock.Mockito
import com.protose.resque._
import com.protose.resque.Machine._
import java.net.InetAddress
import java.lang.management.ManagementFactory

object MachineSpec extends Specification with Mockito {
    "returns the hostname" in {
        hostname must_== InetAddress.getLocalHost.getHostName
    }
    "returns the pid" in {
        val expectedPid = ManagementFactory.getRuntimeMXBean.getName.split("@").first
        pid must_== expectedPid
    }
}


// vim: set ts=4 sw=4 et:
