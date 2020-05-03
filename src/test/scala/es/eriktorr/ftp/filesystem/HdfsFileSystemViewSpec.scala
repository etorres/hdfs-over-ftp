package es.eriktorr.ftp.filesystem

import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.hadoop.fs.permission.FsPermission
import org.apache.hadoop.fs.{FileStatus, Path}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFileSystemViewSpec extends UnitSpec with DataProvider {
  "HDFS filesystem" should "get home directory" in {
    anonymousHdfsFileSystemView().getHomeDirectory shouldBe HdfsFtpFile(
      DistributedFileSystemFake,
      "/user/ftp/pub",
      AnonymousFtpUser,
      CustomHdfsLimits
    )
  }

  it should "get working directory" in {
    anonymousHdfsFileSystemView().getWorkingDirectory shouldBe HdfsFtpFile(
      DistributedFileSystemFake,
      "/user/ftp/pub",
      AnonymousFtpUser,
      CustomHdfsLimits
    )
  }

  it should "change working directory to absolute path" in {
    val absolutePath = "/user/root/input"
    val hdfsFileSystemView = anonymousHdfsFileSystemView()
    val changed = hdfsFileSystemView.changeWorkingDirectory(absolutePath)
    val workingDirectory = hdfsFileSystemView.getWorkingDirectory
    (changed, workingDirectory) shouldBe (
      (
        true,
        HdfsFtpFile(
          DistributedFileSystemFake,
          absolutePath,
          AnonymousFtpUser,
          CustomHdfsLimits
        )
      )
    )
  }

  it should "change working directory to relative path" in {
    val relativePath = "input"
    val hdfsFileSystemView = anonymousHdfsFileSystemView()
    val changed = hdfsFileSystemView.changeWorkingDirectory(relativePath)
    val workingDirectory = hdfsFileSystemView.getWorkingDirectory
    (changed, workingDirectory) shouldBe (
      (
        true,
        HdfsFtpFile(
          DistributedFileSystemFake,
          s"/user/ftp/pub/$relativePath",
          AnonymousFtpUser,
          CustomHdfsLimits
        )
      )
    )
  }

  it should "get a file" in {
    anonymousHdfsFileSystemView().getFile("/user/root") shouldBe HdfsFtpFile(
      DistributedFileSystemFake,
      "/user/root",
      AnonymousFtpUser,
      CustomHdfsLimits
    )
  }

  it should "check whether file is random accessible" in {
    anonymousHdfsFileSystemView().isRandomAccessible shouldBe true
  }

  private[this] lazy val CustomHdfsLimits = HdfsLimits(maxListedFiles = 1000)

  private[this] def anonymousHdfsFileSystemView() =
    new HdfsFileSystemView(
      DistributedFileSystemFake,
      AnonymousFtpUser,
      HdfsClientConfig(
        uri = "hdfs://localhost:9000",
        superUser = "hadoop",
        superGroup = "supergroup",
        makeHomeRoot = false,
        hdfsLimits = CustomHdfsLimits
      )
    )

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
