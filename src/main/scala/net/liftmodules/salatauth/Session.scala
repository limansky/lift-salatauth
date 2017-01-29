package net.liftmodules.salatauth

import org.bson.types.ObjectId
import java.util.Date
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.http.S
import net.liftweb.common.Full
import net.liftweb.common.Empty
import org.joda.time.DateTime
import salat._
import salat.global._
import com.mongodb.casbah.query.Imports._
import net.liftweb.common.Box
import java.util.UUID
import net.liftweb.util.Helpers

/**
 * Represents user session in database
 */
case class Session[UserIdType](
  _id: UUID,
  userId: UserIdType,
  aliveTill: Date
)

/**
 * Helper object to manage sessions.
 *
 * Usually you don't need to use this class, since it called by LoginManager.
 */
object Session {
  private lazy val collection = SalatAuth.sessionsCollection.vend
  private lazy val cookieName = SalatAuth.sessionCookieName.vend
  private lazy val cookiePath = SalatAuth.sessionCookiePath.vend
  private lazy val cookieDomain = SalatAuth.sessionCookieDomain.vend
  private lazy val sessionTtl = SalatAuth.sessionTtl.vend

  /**
   * Removes session cookie and DB entity.
   */
  def dropSession(): Unit = {
    S.findCookie(cookieName).foreach { cookie =>
      val empty = new HTTPCookie(cookieName, Empty, cookieDomain, Full(cookiePath),
        Full(0), Empty, Empty)
      S.addCookie(empty)
      cookie.value.flatMap(v => Helpers.tryo(UUID.fromString(v))).foreach(id =>
        collection.map(_.remove(MongoDBObject("_id" -> id))))
    }
  }

  /**
   * Creates session for the user specified by the id.
   *
   * @param uid user id to create session.
   */
  def createSession[UserIdType](uid: UserIdType)(implicit m: Manifest[UserIdType]): Unit = collection foreach { c =>
    val date = (new DateTime).plus(sessionTtl)
    val session = Session(UUID.randomUUID, uid, date.toDate())
    val dbo = grater[Session[UserIdType]].asDBObject(session)
    c.save(dbo)
    val cookie = new HTTPCookie(cookieName, Full(session._id.toString), cookieDomain, Full(cookiePath),
      Full(sessionTtl.toPeriod().toStandardSeconds().getSeconds()), Empty, Empty)
    S.addCookie(cookie)
  }

  /**
   * Returns session instance if it found in cookie and the database.
   */
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

      case Some(es) if es.aliveTill.compareTo(now) == -1 =>
        dropSession
        None

      case _ => s
    }
  }
}