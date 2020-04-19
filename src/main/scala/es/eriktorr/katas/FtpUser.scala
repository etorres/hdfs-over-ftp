package es.eriktorr.katas

import java.util

import es.eriktorr.katas.authorities.{
  LoginAuthorizationMaker,
  TransferRateAuthorizationMaker,
  WriteAuthorizationMaker
}
import org.apache.ftpserver.ftplet.{Authority, AuthorizationRequest, User}

import scala.jdk.CollectionConverters._

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
final case class FtpUser(
  name: String,
  password: String,
  maxLoginPerIp: Int = 2,
  maxLoginNumber: Int = 4,
  maxIdleTime: Int = 300,
  maxDownloadRate: Int = 0,
  maxUploadRate: Int = 0,
  writePermission: Boolean = false,
  enabled: Boolean = true
) extends User {
  private[this] val authorizationMakers =
    List(WriteAuthorizationMaker, LoginAuthorizationMaker, TransferRateAuthorizationMaker)

  private[this] lazy val authorities =
    authorizationMakers.filter(_.appliesTo(this)).map(_.authorityFrom(this))

  override def getName: String = name

  override def getPassword: String = password

  override def getAuthorities: util.List[_ <: Authority] = authorities.asJava

  override def getAuthorities(clazz: Class[_ <: Authority]): util.List[_ <: Authority] =
    authorities.collect { case x if x.getClass == clazz => x }.asJava

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  override def authorize(request: AuthorizationRequest): AuthorizationRequest =
    authorities
      .find(_.canAuthorize(request))
      .flatMap(authority => Option(authority.authorize(request)))
      .orNull

  override def getMaxIdleTime: Int = maxIdleTime

  override def getEnabled: Boolean = enabled

  override def getHomeDirectory: String = s"/home/$name"
}
