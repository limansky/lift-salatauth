/*
 * Copyright 2013 Mike Limansky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftmodules.salatauth

import net.liftweb.util.Helpers._
import org.mindrot.jbcrypt.BCrypt
import com.mongodb.casbah.query.Imports._
import org.bson.types.ObjectId
import com.novus.salat._
import com.novus.salat.global._

/**
 * User entity prototype.
 *
 * You should define your entity extending this class. For example:
 * {{{
 * case class User(
 *   val _id: ObjectId,
 *   override val username: String,
 *   override val password: String,
 *   val realName: String,
 *   val email: String,
 *   override val roles: Set[String],
 *   val phone: String
 * ) extends ProtoUser(username, password, roles)
 * }}}
 *
 * To allow your roles be serialized you have to set SalatAuth.rolesCollections
 * in your Boot.scala.
 */
abstract class ProtoUser(
    val username: String,
    val password: String,
    val roles: Set[String]) {

  val rolesCollection = SalatAuth.rolesCollection.vend

  /**
   * Checks if the password match with this user password
   * @param pass Password to be checked
   */
  def passwordMatch(pass: String): Boolean = {
    if (pass.length > 0 && password.length > 0) {
      tryo(BCrypt.checkpw(pass, password)).openOr(false)
    } else false
  }

  /**
   * Searches for roles in MongoDB
   */
  def findRoles(query: DBObject): List[Role] = {
    rolesCollection.toList.flatMap(_.find(query).map(dbo => grater[Role].asObject(dbo)).toList)
  }

  lazy val perms = findRoles("_id" $in roles).flatMap(_.permissions).toSet

  /**
   * Returns a set of permissions for this user.
   */
  def permissions = perms
}

object ProtoUser {

  /**
   * Helper function to hash password
   *
   * @param password to be hashed
   */
  def hashPassword(password: String): String =
    tryo(BCrypt.hashpw(password, BCrypt.gensalt(10))).openOr("")
}
