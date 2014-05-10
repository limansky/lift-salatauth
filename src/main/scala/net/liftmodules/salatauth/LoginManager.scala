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
import net.liftweb.common.{ Box, Empty, Full }
import net.liftweb.http.Req

/**
 * Base login manager
 *
 * In most cases you should use this trait as basic for your login mananges.
 */
trait LoginManager[UserType <: ProtoUser, UserIdType] {

  /**
   * Search user by given ID
   *
   * @param id user id to be searched
   */
  def findUserById(id: UserIdType): Option[UserType]

  /**
   * Returns ID of the given user
   */
  def getUserId(user: UserType): UserIdType

  private object curUserId extends SessionVar[Box[UserIdType]](Empty)
  private object authenticated extends SessionVar[Boolean](false)
  private object curUser extends RequestVar[Box[UserType]](curUserId.flatMap(findUserById))
      with CleanRequestVarOnSessionTransition {

    override lazy val __nameSalt = Helpers.nextFuncName
    override def logUnreadVal = false
  }

  /**
   * List of additional actions to be performed on user login
   */
  def onLogIn: List[UserType => Unit] = Nil

  /**
   * List of additional actions to be performed on user logout.
   */
  def onLogOut: List[Box[UserType] => Unit] = Nil

  /**
   * Currently logged in user id
   */
  def currentUserId: Box[UserIdType] = curUserId.is

  /**
   * Returns currently logged in user
   */
  def currentUser: Box[UserType] = curUser.is

  def userAuthenticateded: Boolean = authenticated.is

  /**
   * Log user in to system
   *
   * @param user user to be logged in
   * @param authenticate shows if user was authenticated
   * @param remember shows if the session shall be created
   */
  def logUserIn(user: UserType, authenticate: Boolean = false, remember: Boolean = false)(implicit m: Manifest[UserIdType]): Unit = {
    curUserId.remove
    curUser.remove
    curUserId(Full(getUserId(user)))
    curUser(Full(user))
    authenticated(authenticate)
    onLogIn.foreach(_(user))
    if (remember) {
      Session.createSession(getUserId(user))
    }
  }

  /**
   * Log user out
   */
  def logUserOut(): Unit = {
    onLogOut.foreach(_(currentUser))
    curUserId.remove
    curUser.remove
    authenticated.remove
    Session.dropSession
    S.session.foreach(_.destroySession)
  }

  /**
   * Checks if there any user logged in.
   */
  def isLoggedIn: Boolean = currentUserId.isDefined

  /**
   * Checks if the current user has permission
   *
   * @param permission required permission
   */
  def hasPermission(permission: Permission): Boolean = {
    currentUser.map(u => permission.implies(u.permissions)).openOr(false)
  }

  /**
   * Checks if the current user has specified role
   *
   * @param role required role
   */
  def hasRole(role: String): Boolean = {
    currentUser.map(_.roles.contains(role)).openOr(false)
  }

  /**
   * Check if there is an existing session and log user in if it's
   * not expired. Usually you should setup it as early stateful hook:
   * {{{
   *   LiftRules.earlyInStateful.append(MyLoginManager.checkSession)
   * }}}
   */
  def checkSession(req: Box[Req])(implicit m: Manifest[UserIdType]): Unit = {
    Session.get.flatMap(s => findUserById(s.userId)).foreach(user => logUserIn(user, false))
  }

}
