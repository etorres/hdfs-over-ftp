package es.eriktorr.ftp.usermanagement.authorization

import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission

final class ConcurrentLoginAuthority(val maxLoginNumber: Int, val maxLoginPerIp: Int)
    extends ConcurrentLoginPermission(maxLoginNumber, maxLoginPerIp) {
  override def equals(that: Any): Boolean = that match {
    case that: ConcurrentLoginAuthority =>
      this.maxLoginNumber == that.maxLoginNumber &&
        this.maxLoginPerIp == that.maxLoginPerIp
    case _ => false
  }

  override def hashCode(): Int =
    IntHashCode.hashCode(Seq(maxLoginNumber, maxLoginPerIp))

  override def toString: String =
    s"${classOf[ConcurrentLoginAuthority].getSimpleName}(maxLoginNumber=${maxLoginNumber.toString},maxLoginPerIp=${maxLoginPerIp.toString})"
}
