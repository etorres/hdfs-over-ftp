package es.eriktorr.katas

import org.apache.ftpserver.ftplet.{Authentication, User}
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor
import org.apache.ftpserver.usermanager.impl.AbstractUserManager

class FtpUsers(applicationContext: ApplicationContext)
    extends AbstractUserManager("root", new SaltedPasswordEncryptor) {
  override def getUserByName(username: String): User =
    applicationContext.ftpUsers.find(_.name == username).getOrElse(UnauthorizedUser)

  override def getAllUserNames: Array[String] =
    applicationContext.ftpUsers.map(_.name).toArray

  override def delete(username: String): Unit = {}

  override def save(user: User): Unit = {}

  override def doesExist(username: String): Boolean =
    applicationContext.ftpUsers.exists(_.name == username)

  override def authenticate(authentication: Authentication): User = ???
  /*
  authentication match {
    case UsernamePasswordAuthentication => null
    case AnonymousAuthentication => null
    case _ => null
  }
 */
}
