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

  it should "fail with exception when changing working directory out of chroot jail" in {
    the[IllegalArgumentException] thrownBy chrootJailHdfsFileSystemView().changeWorkingDirectory(
      "/user/root"
    ) should have message "requirement failed: Access is restricted to home directory"
  }

  it should "fail with exception when accessing directories/files out of chroot jail" in {
    the[IllegalArgumentException] thrownBy chrootJailHdfsFileSystemView().getFile(
      "/user/root/file.txt"
    ) should have message "requirement failed: Access is restricted to home directory"
  }

  it should "change to a working directory inside the chroot jail" in {
    chrootJailHdfsFileSystemView().changeWorkingDirectory("/user/ftp/pub/dir") shouldBe true
  }

  it should "get a file inside the chroot jail" in {
    chrootJailHdfsFileSystemView().getFile("/user/ftp/pub/file.txt") shouldBe HdfsFtpFile(
      DistributedFileSystemFake,
      "/user/ftp/pub/file.txt",
      AnonymousFtpUser,
      CustomHdfsLimits
    )
  }

  private[this] lazy val CustomHdfsLimits = HdfsLimits(maxListedFiles = 1000)

  private[this] def anonymousHdfsFileSystemView() = aHdfsFileSystemView(false)

  private[this] def chrootJailHdfsFileSystemView() = aHdfsFileSystemView(true)

  private[this] def aHdfsFileSystemView(enableChrootJail: Boolean) =
    new HdfsFileSystemView(
      DistributedFileSystemFake,
      AnonymousFtpUser,
      HdfsClientConfig(
        uri = "hdfs://localhost:9000",
        superUser = "hadoop",
        superGroup = "supergroup",
        enableChrootJail = enableChrootJail,
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
