package net.liftmodules.salatauth

import net.liftweb.util.Helpers._
import org.mindrot.jbcrypt.BCrypt
import com.mongodb.casbah.query.Imports._

abstract class ProtoUser(val username: String,
                         val password: String,
                         val email: String,
                         val roles: Set[String]) {

  def passwordMatch(pass: String): Boolean = {
    if (pass.length > 0 && password.length > 0) {
      tryo(BCrypt.checkpw(pass, password)).openOr(false)
    } else false
  }

  def findRoles(query: DBObject): List[Role]

  lazy val perms = findRoles("_id" $in roles).flatMap(_.permissions).toSet
  def permissions = perms
}
