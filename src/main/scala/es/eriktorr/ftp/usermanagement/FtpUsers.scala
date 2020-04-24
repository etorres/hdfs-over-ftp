package es.eriktorr.ftp.usermanagement

import org.apache.ftpserver.ftplet.{Authentication, AuthenticationFailedException, User}
import org.apache.ftpserver.usermanager.impl.AbstractUserManager
import org.apache.ftpserver.usermanager.{
  AnonymousAuthentication,
  SaltedPasswordEncryptor,
  UsernamePasswordAuthentication
}

class FtpUsers(ftpUsers: Seq[FtpUser])
    extends AbstractUserManager("admin", new SaltedPasswordEncryptor) {
  // Needed because the Java interface will return null if the user does not exist
  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  override def getUserByName(username: String): User =
    ftpUsers.find(_.name == username).orNull

  override def getAllUserNames: Array[String] = ftpUsers.map(_.name).toArray

  override def delete(username: String): Unit = {}

  override def save(user: User): Unit = {}

  override def doesExist(username: String): Boolean = ftpUsers.exists(_.name == username)

  // Needed because the Java interface will fail with an exception on authenticated requests
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  override def authenticate(authentication: Authentication): User =
    authentication match {
      case usernamePasswordAuthentication: UsernamePasswordAuthentication =>
        val candidateUser = getUserByName(usernamePasswordAuthentication.getUsername)
        if (candidateUser == null)
          throw new AuthenticationFailedException("Authentication failed")
        else {
          if (getPasswordEncryptor.matches(
              usernamePasswordAuthentication.getPassword,
              candidateUser.getPassword
            )) candidateUser
          else throw new AuthenticationFailedException("Authentication failed")
        }
      case _: AnonymousAuthentication =>
        getUserByName("anonymous")
      case _ =>
        throw new IllegalArgumentException("Authentication not supported by this user manager")
    }
}
