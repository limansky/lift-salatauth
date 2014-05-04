package net.liftmodules.salatauth

import org.bson.types.ObjectId
import java.util.Date
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.http.S
import net.liftweb.common.Full
import net.liftweb.common.Empty
import org.joda.time.DateTime
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.query.Imports._
import net.liftweb.common.Box
import java.util.UUID
import net.liftweb.util.Helpers

case class Session[UserIdType](
  _id: UUID,
  userId: UserIdType,
  aliveTill: Date)

object Session {
  private lazy val collection = SalatAuth.sessionsCollection.vend
  private lazy val cookieName = SalatAuth.sessionCookieName.vend
  private lazy val cookiePath = SalatAuth.sessionCookiePath.vend
  private lazy val cookieDomain = SalatAuth.sessionCookieDomain.vend
  private lazy val sessionTtl = SalatAuth.sessionTtl.vend

  def dropSession(): Unit = {
    S.findCookie(cookieName).foreach { cookie =>
      val empty = new HTTPCookie(cookieName, Empty, cookieDomain, Full(cookiePath),
        Full(0), Empty, Empty)
      S.addCookie(empty)
    }
  }

  def createSession[UserIdType](uid: UserIdType)(implicit m: Manifest[UserIdType]): Unit = collection foreach { c =>
    val date = (new DateTime).plus(sessionTtl)
    val session = Session(UUID.randomUUID, uid, date.toDate())
    val dbo = grater[Session[UserIdType]].asDBObject(session)
    c.save(dbo)
    val cookie = new HTTPCookie(cookieName, Full(session._id.toString), cookieDomain, Full(cookiePath),
      Full(sessionTtl.toPeriod().toStandardSeconds().getSeconds()), Empty, Empty)
    S.addCookie(cookie)
  }

  def get[UserIdType]()(implicit m: Manifest[UserIdType]): Option[Session[UserIdType]] = {
    val s = for {
      coll <- collection
      c <- S.findCookie(cookieName)
      cv <- c.value
      id <- Helpers.tryo(UUID.fromString(cv))
      dbo <- coll.findOneByID(id)
    } yield grater[Session[UserIdType]].asObject(dbo)

    val now = new Date

    s match {
      case None =>
        dropSession
        None

      case Some(es) if es.aliveTill.compareTo(now) == 1 =>
        dropSession
        None

      case _ => s
    }
  }
}