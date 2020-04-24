package es.eriktorr.ftp

import es.eriktorr.ftp.unitspec.UnitSpec
import org.apache.commons.daemon.{DaemonContext, DaemonController}

class FtpServerDaemonSpec extends UnitSpec {
  "ftp server daemon" should "parse command-line arguments" in {
    val daemon = new FtpServerDaemon
    daemon.init(new DaemonContextStub())
  }

  final class DaemonContextStub extends DaemonContext {
    override def getController: DaemonController = new DaemonControllerFake

    override def getArguments: Array[String] = Array("-config", "application.config", "123")
  }

  final class DaemonControllerFake extends DaemonController {
    override def shutdown(): Unit = {}

    override def reload(): Unit = {}

    override def fail(): Unit = {}

    override def fail(message: String): Unit = {}

    override def fail(exception: Exception): Unit = {}

    override def fail(message: String, exception: Exception): Unit = {}
  }
}
