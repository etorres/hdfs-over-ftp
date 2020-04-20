package es.eriktorr.katas

import java.util

import es.eriktorr.katas.authorities.{
  LoginAuthorizationMaker,
  TransferRateAuthorizationMaker,
  WriteAuthorizationMaker
}
import org.apache.ftpserver.ftplet.{Authority, AuthorizationRequest, User}

import scala.jdk.CollectionConverters._

sealed case class FtpUser(
  name: String,
  password: String,
  maxLoginPerIp: Option[Int],
  maxLoginNumber: Option[Int],
  maxIdleTime: Option[Int],
  maxDownloadRate: Option[Int],
  maxUploadRate: Option[Int],
  writePermission: Option[Boolean],
  enabled: Option[Boolean]
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

  override def getMaxIdleTime: Int = maxIdleTime.getOrElse(FtpUser.MaxIdleTimeDefault)

  override def getEnabled: Boolean = enabled.getOrElse(FtpUser.EnabledDefault)

  override def getHomeDirectory: String = s"/home/$name"
}

object FtpUser {
  val MaxIdleTimeDefault: Int = 300
  val EnabledDefault: Boolean = true
}
