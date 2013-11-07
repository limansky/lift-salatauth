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

import net.liftweb.http.SessionVar
import net.liftweb.http.RequestVar
import net.liftweb.http.CleanRequestVarOnSessionTransition
import net.liftweb.http.S
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._
import net.liftweb.common.{Box, Empty, Full}
import org.mindrot.jbcrypt.BCrypt

trait LoginManager[UserIdType, UserType <: ProtoUser] {

  def findUserById(id: UserIdType): Option[UserType]
  def getUserId(user: UserType): UserIdType

  // current userId stored in the session.
  private object curUserId extends SessionVar[Box[UserIdType]](Empty)
  private object curUser extends RequestVar[Box[UserType]](curUserId.flatMap(findUserById))
    with CleanRequestVarOnSessionTransition {

    override lazy val __nameSalt = Helpers.nextFuncName
  }

  def onLogIn: List[UserType => Unit] = Nil
  def onLogOut: List[Box[UserType] => Unit] = Nil

  def currentUserId: Box[UserIdType] = curUserId.is
  def currentUser: Box[UserType] = curUser.is

  def logUserIn(user: UserType): Unit = {
    curUserId.remove
    curUser.remove
    curUserId(Full(getUserId(user)))
    curUser(Full(user))
    onLogIn.foreach(_(user))
  }

  def logUserOut(): Unit = {
    onLogOut.foreach(_(currentUser))
    curUserId.remove
    curUser.remove
    S.session.foreach(_.destroySession)
  }

  def isLoggedIn: Boolean = currentUserId.isDefined

  def hasPermission(permission: Permission): Boolean = {
    currentUser.map(u => permission.implies(u.permissions)).openOr(false)
  }

  def hashPassword(password: String): String =
    tryo(BCrypt.hashpw(password, BCrypt.gensalt(10))).openOr("")
}
