package es.eriktorr.katas

import org.apache.ftpserver.ftplet.{Authentication, User}
import org.apache.ftpserver.usermanager.{
  AnonymousAuthentication,
  SaltedPasswordEncryptor,
  UsernamePasswordAuthentication
}
import org.apache.ftpserver.usermanager.impl.AbstractUserManager

class FtpUsers(applicationContext: ApplicationContext)
    extends AbstractUserManager("admin", new SaltedPasswordEncryptor) {
  override def getUserByName(username: String): User =
    applicationContext.ftpUsers.find(_.name == username).getOrElse(ForbiddenUser)

  override def getAllUserNames: Array[String] =
    applicationContext.ftpUsers.map(_.name).toArray

  override def delete(username: String): Unit = {}

  override def save(user: User): Unit = {}

  override def doesExist(username: String): Boolean =
    applicationContext.ftpUsers.exists(_.name == username)

  override def authenticate(authentication: Authentication): User =
    authentication match {
      case usernamePasswordAuthentication: UsernamePasswordAuthentication =>
        val candidateUser = getUserByName(usernamePasswordAuthentication.getUsername)
        if (candidateUser == ForbiddenUser) ForbiddenUser
        else {
          if (getPasswordEncryptor.matches(
              usernamePasswordAuthentication.getPassword,
              candidateUser.getPassword
            )) candidateUser
          else ForbiddenUser
        }
      case _: AnonymousAuthentication =>
        getUserByName("anonymous")
      case _ => ForbiddenUser
    }
}
