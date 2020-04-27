package es.eriktorr.ftp.filesystem

import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.hadoop.fs.permission.FsPermission
import org.apache.hadoop.fs.{FileStatus, Path}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFileSystemViewSpec extends UnitSpec with DataProvider {
  private[this] val hdfsFileSystemView =
    new HdfsFileSystemView(DistributedFileSystemFake, AnonymousFtpUser)

  "HDFS filesystem" should "get home directory" in {
    hdfsFileSystemView.getHomeDirectory shouldBe HdfsFtpFile(
      DistributedFileSystemFake,
      "/",
      AnonymousFtpUser
    )
  }

  it should "get working directory" in {
    hdfsFileSystemView.getWorkingDirectory shouldBe HdfsFtpFile(
      DistributedFileSystemFake,
      "/",
      AnonymousFtpUser
    )
  }

  it should "change working directory" in {
    val changed = hdfsFileSystemView.changeWorkingDirectory("/user/root")
    val workingDirectory = hdfsFileSystemView.getWorkingDirectory
    (changed, workingDirectory) shouldBe (
      (
        true,
        HdfsFtpFile(
          DistributedFileSystemFake,
          "/user/root",
          AnonymousFtpUser
        )
      )
    )
  }

  it should "get a file" in {
    hdfsFileSystemView.getFile("/user/root") shouldBe HdfsFtpFile(
      DistributedFileSystemFake,
      "/user/root",
      AnonymousFtpUser
    )
  }

  it should "check whether file is random accessible" in {
    hdfsFileSystemView.isRandomAccessible shouldBe true
  }

  object DistributedFileSystemFake extends DistributedFileSystem {
    override def getFileStatus(f: Path): FileStatus = new FileStatus(
      256L,
      true,
      3,
      128000000L,
      1587995794L,
      1587995794L,
      FsPermission.createImmutable(7),
      "root",
      "supergroup",
      new Path("/hadoop/user/root")
    )
  }
}
