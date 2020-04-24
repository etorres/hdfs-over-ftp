package es.eriktorr.ftp.usermanagement.authorization

import org.apache.ftpserver.usermanager.impl.TransferRatePermission

final class TransferRateAuthority(val maxDownloadRate: Int, val maxUploadRate: Int)
    extends TransferRatePermission(maxDownloadRate, maxUploadRate) {
  override def equals(that: Any): Boolean = that match {
    case that: TransferRateAuthority =>
      this.maxDownloadRate == that.maxDownloadRate &&
        this.maxUploadRate == that.maxUploadRate
    case _ => false
  }

  override def hashCode(): Int =
    HashCode.hashCode(Seq(maxDownloadRate, maxUploadRate))

  override def toString: String =
    s"${classOf[TransferRateAuthority].getSimpleName}(maxDownloadRate=${maxDownloadRate.toString},maxUploadRate=${maxUploadRate.toString})"
}
