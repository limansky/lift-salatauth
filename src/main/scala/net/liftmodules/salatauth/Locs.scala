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

import net.liftweb.sitemap.Loc._
import net.liftweb.http.RedirectResponse
import net.liftweb.http.S
import net.liftweb.http.RedirectWithState
import net.liftweb.http.RedirectState
import net.liftweb.http.SessionVar
import net.liftweb.common.{ Box, Empty, Full }
import net.liftweb.util.Helpers

trait Locs[UserIdType, UserType <: ProtoUser] {
  val loginManager: LoginManager[UserIdType, UserType]
  val indexUrl = "/"
  val loginUrl = "/login"

  protected def DisplayError(message: String) = () =>
    RedirectWithState(indexUrl, RedirectState(() => S.error(S ? message)))

  def RedirectToIndex = RedirectResponse(indexUrl)

  def RedirectToIndexWithCookies =
    RedirectResponse(indexUrl, S.responseCookies: _*)

  def RedirectToLoginWithReferrer = {
    val uri = S.uriAndQueryString
    RedirectWithState(loginUrl, RedirectState(() => { LoginRedirect.set(uri) }))
  }

  def HasPermission(permission: Permission) = If(
    () => loginManager.hasPermission(permission), () => RedirectToLoginWithReferrer
  )

  def HasAnyPermission(permissions: Permission*) = If({
    val ps = permissions.toList
    () => ps.exists(loginManager.hasPermission)
  }, () => RedirectToLoginWithReferrer
  )

  def RequireLoggedIn = If(
    () => loginManager.isLoggedIn, () => RedirectToLoginWithReferrer
  )

  protected def logoutLocParams = RequireLoggedIn ::
    EarlyResponse(() => {
      if (loginManager.isLoggedIn) { loginManager.logUserOut() }
      Full(RedirectToIndexWithCookies)
    }) :: Nil

}

/**
 * SessionVar contains requested URI when user was redirected to login page.
 *
 * For example, somewhere in your login page snippet:
 * {{{
 *   def loginUser(user: String, password: String) = {
 *     UserDAO.findOne(MongoDBObject("username" -> user)) match {
 *       case Some(u) if (u.passwordMatch(password)) => S.redirectTo(LoginRedirect or "")
 *       case _ => S.error("Invalid user name or password")
 *     }
 *   }
 * }}}
 */
object LoginRedirect extends SessionVar[Box[String]](Empty) {
  override def __nameSalt = Helpers.nextFuncName
}
