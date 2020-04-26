package es.eriktorr.ftp.filesystem.permissions

import es.eriktorr.ftp.unitspec.ParameterizedUnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.ftpserver.ftplet.User

class OtherPermissionSpec extends ParameterizedUnitSpec with DataProvider {
  "Other permission" should "authorize read/write access to file based only on file attributes" in {
    val readPermissionValidator: (FileAttributes, User) => Boolean =
      (fileAttributes: FileAttributes, user: User) => OtherPermission.canRead(fileAttributes, user)
    val writePermissionValidator: (FileAttributes, User) => Boolean =
      (fileAttributes: FileAttributes, user: User) => OtherPermission.canWrite(fileAttributes, user)
    val testCases = Table(
      ("file-attributes", "user", "permission-validator", "decision"),
      (
        FileAttributes(owner = "nobody", group = "nobody", permissions = "------r--"),
        RootFtpUser,
        readPermissionValidator,
        true
      ),
      (
        FileAttributes(owner = "nobody", group = "nobody", permissions = "---------"),
        RootFtpUser,
        readPermissionValidator,
        false
      ),
      (
        FileAttributes(owner = "nobody", group = "nobody", permissions = "-------w-"),
        RootFtpUser,
        writePermissionValidator,
        true
      ),
      (
        FileAttributes(owner = "nobody", group = "nobody", permissions = "---------"),
        RootFtpUser,
        writePermissionValidator,
        false
      )
    )
    forAll(testCases)(
      (
        fileAttributes: FileAttributes,
        user: User,
        permissionMaker: (FileAttributes, User) => Boolean,
        decision: Boolean
      ) => permissionMaker(fileAttributes, user) shouldBe decision
    )
  }
}
