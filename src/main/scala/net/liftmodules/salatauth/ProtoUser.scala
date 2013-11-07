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
