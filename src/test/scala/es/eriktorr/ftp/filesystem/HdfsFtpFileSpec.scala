package es.eriktorr.ftp.filesystem

import es.eriktorr.ftp.unitspec.UnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.hadoop.fs.{FileStatus, Path}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HdfsFtpFileSpec extends UnitSpec with DataProvider {
  "HDFS ftp file" should "fail with user exception when max limit for directory listing is exceeded" in {
    val hdfsFtpFile = HdfsFtpFile(
      DistributedFileSystemFake,
      "/usr/anonymoys",
      AnonymousFtpUser,
      HdfsLimits(maxListedFiles = 2)
    )
    the[IllegalArgumentException] thrownBy hdfsFtpFile
      .listFiles() should have message "requirement failed: List files is supported for directories with at most 2 files"
  }

  object DistributedFileSystemFake extends DistributedFileSystem {
    override def listStatus(p: Path): Array[FileStatus] =
      Array(aFileStatus("/a"), aFileStatus("/b"), aFileStatus("/c"))
  }

  private[this] def aFileStatus(fileName: String) =
    new FileStatus(128L, false, 3, 128000000L, 1587995794L, new Path(fileName))
}
