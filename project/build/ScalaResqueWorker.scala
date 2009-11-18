import sbt._

class ScalaResqueWorkerProject(info: ProjectInfo) extends DefaultProject(info) {
    val specs        = "org.specs" % "specs" % "1.6.0" from "http://specs.googlecode.com/files/specs-1.6.0.jar"
    val mongodb      = "com.mongodb" % "mongodb" % "1.0" from "http://cloud.github.com/downloads/mongodb/mongo-java-driver/mongo-1.0.jar"
    val mockito      = "org.mockito" % "mockito" % "1.8.0" from "http://mockito.googlecode.com/files/mockito-all-1.8.0.jar"
}

// vim: set ts=4 sw=4 et:
