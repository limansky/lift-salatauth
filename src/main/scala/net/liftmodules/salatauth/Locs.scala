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
import net.liftweb.sitemap.{ Menu, Loc }
import net.liftweb.http.RedirectResponse
import net.liftweb.http.S
import net.liftweb.http.RedirectWithState
import net.liftweb.http.RedirectState
import net.liftweb.http.SessionVar
import net.liftweb.common.{ Box, Empty, Full }
import net.liftweb.util.Helpers

object Locs extends Locs

/**
 * This trait contains different LocParams related to permission control.
 */
trait Locs {
  private lazy val loginManager = SalatAuth.loginManager.vend
  private lazy val indexUrl = SalatAuth.indexUrl.vend
  private lazy val loginUrl = SalatAuth.loginUrl.vend
  private lazy val logoutUrl = SalatAuth.logoutUrl.vend

  /**
   * Redirects to index page and show error message.
   *
   * @param message message to be passed to S.error
   */
  def RedirectWithError(message: String) = () =>
    RedirectWithState(indexUrl, RedirectState(() => S.error(S ? message)))

  /**
   * Redirects to index page.
   */
  def RedirectToIndex = RedirectResponse(indexUrl)

  /**
   * Redirects to index page with cookies.
   */
  def RedirectToIndexWithCookies =
    RedirectResponse(indexUrl, S.responseCookies: _*)

  /**
   * Redirects to login page saving requested uri in LoginRedirect variable
   */
  def RedirectToLoginWithReferrer = {
    val uri = S.uriAndQueryString
    RedirectWithState(loginUrl, RedirectState(() => { LoginRedirect.set(uri) }))
  }

  /**
   * Allows access if current user has requested permission.
   */
  def HasPermission(permission: Permission) = If(
    () => loginManager.hasPermission(permission), () => RedirectToLoginWithReferrer
  )

  /**
   * Allows access if current user has requested role.
   */
  def HasRole(role: String) = If(
    () => loginManager.hasRole(role), () => RedirectToLoginWithReferrer
  )

  /**
   * Allows access if current user has any of requested permissions.
   */
  def HasAnyPermission(permissions: Permission*) = If({
    val ps = permissions.toList
    () => ps.exists(loginManager.hasPermission)
  }, () => RedirectToLoginWithReferrer
  )

  /**
   * Allows access if the user is logged in.
   */
  def RequireLoggedIn = If(
    () => loginManager.isLoggedIn, () => RedirectToLoginWithReferrer
  )

  /**
   * Allows access if user is not logged in.
   */
  def RequireNotLoggedIn = If(
    () => !loginManager.isLoggedIn, () => RedirectToIndex
  )

  /**
   * Provides a list of LocParam required to create logout menu item.
   */
  def logoutLocParams = RequireLoggedIn ::
    EarlyResponse(() => {
      if (loginManager.isLoggedIn) { loginManager.logUserOut() }
      Full(RedirectToIndexWithCookies)
    }) :: Nil

  def buildLogin(text: String, params: List[LocParam[Unit]]) = Menu(Loc(
    "login",
    loginUrl.split("/").toList.filterNot(_.isEmpty),
    S.?(text),
    params
  ))

  def buildLogout(text: String, params: List[LocParam[Unit]]) = Menu(Loc(
    "logout",
    logoutUrl.split("/").toList.filterNot(_.isEmpty),
    S.?(text),
    params ::: logoutLocParams
  ))
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
