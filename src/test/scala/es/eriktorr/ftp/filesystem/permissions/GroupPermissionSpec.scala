package es.eriktorr.ftp.filesystem.permissions

import es.eriktorr.ftp.unitspec.ParameterizedUnitSpec
import es.eriktorr.ftp.unitspec.data.DataProvider
import org.apache.ftpserver.ftplet.User

class GroupPermissionSpec extends ParameterizedUnitSpec with DataProvider {
  "Group permission" should "authorize read/write access to file depending on group membership" in {
    val readPermissionValidator: (FileAttributes, User) => Boolean =
      (fileAttributes: FileAttributes, user: User) => GroupPermission.canRead(fileAttributes, user)
    val writePermissionValidator: (FileAttributes, User) => Boolean =
      (fileAttributes: FileAttributes, user: User) => GroupPermission.canWrite(fileAttributes, user)
    val testCases = Table(
      ("file-attributes", "user", "permission-validator", "decision"),
      (
        FileAttributes(owner = "nobody", group = "supergroup", permissions = "---r-----"),
        RootFtpUser,
        readPermissionValidator,
        true
      ),
      (
        FileAttributes(owner = "root", group = "nobody", permissions = "---r-----"),
        RootFtpUser,
        readPermissionValidator,
        false
      ),
      (
        FileAttributes(owner = "root", group = "supergroup", permissions = "----w----"),
        RootFtpUser,
        writePermissionValidator,
        true
      ),
      (
        FileAttributes(owner = "root", group = "nobody", permissions = "----w----"),
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
