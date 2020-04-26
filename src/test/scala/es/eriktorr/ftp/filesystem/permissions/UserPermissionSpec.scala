package es.eriktorr.ftp.filesystem.permissions

import es.eriktorr.ftp.unitspec.ParameterizedUnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.ftpserver.ftplet.User

class UserPermissionSpec extends ParameterizedUnitSpec with DataProvider {
  "User permission" should "authorize read/write access to file" in {
    val readPermissionValidator: (FileAttributes, User) => Boolean =
      (fileAttributes: FileAttributes, user: User) => UserPermission.canRead(fileAttributes, user)
    val writePermissionValidator: (FileAttributes, User) => Boolean =
      (fileAttributes: FileAttributes, user: User) => UserPermission.canWrite(fileAttributes, user)
    val testCases = Table(
      ("file-attributes", "user", "permission-validator", "decision"),
      (
        FileAttributes(owner = "root", group = "nobody", permissions = "r--------"),
        RootFtpUser,
        readPermissionValidator,
        true
      ),
      (
        FileAttributes(owner = "nobody", group = "nobody", permissions = "r--------"),
        RootFtpUser,
        readPermissionValidator,
        false
      ),
      (
        FileAttributes(owner = "root", group = "nobody", permissions = "-w-------"),
        RootFtpUser,
        writePermissionValidator,
        true
      ),
      (
        FileAttributes(owner = "nobody", group = "nobody", permissions = "-w-------"),
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
