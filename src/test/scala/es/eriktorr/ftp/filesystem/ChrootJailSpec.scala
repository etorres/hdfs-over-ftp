package es.eriktorr.ftp.filesystem

import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.ftpserver.ftplet.FtpException
import org.apache.hadoop.hdfs.DistributedFileSystem

class ChrootJailSpec extends UnitSpec with DataProvider {
  "Chroot jail" should "deny access to files or directories under chroot directory" in {
    val absolutePath = "/user/root/input"
    val hdfsFileSystemView = chrootJailHdfsFileSystemView()
    the[FtpException] thrownBy hdfsFileSystemView.changeWorkingDirectory(
      absolutePath
    ) should have message "Access is restricted to home directory"
  }

  private[this] def chrootJailHdfsFileSystemView() =
    new HdfsFileSystemView(
      DistributedFileSystemFake,
      AnonymousFtpUser,
      HdfsClientConfig(
        uri = "hdfs://localhost:9000",
        superUser = "hadoop",
        superGroup = "supergroup",
        makeHomeRoot = true,
        hdfsLimits = CustomHdfsLimits
      )
    )

  private[this] lazy val CustomHdfsLimits = HdfsLimits(maxListedFiles = 1000)

  object DistributedFileSystemFake extends DistributedFileSystem
}
