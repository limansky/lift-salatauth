package net.liftmodules.salatauth

import net.liftweb.sitemap.Loc._
import net.liftweb.http.RedirectResponse
import net.liftweb.http.S
import net.liftweb.http.RedirectWithState
import net.liftweb.http.RedirectState
import net.liftweb.http.SessionVar
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.util.Helpers

trait Locs[UserIdType, UserType <: ProtoUser] {
  val loginManager: LoginManager[UserIdType, UserType]
  val indexUrl = "/"
  val loginUrl = "/login"

  protected def DisplayError(message: String) = () =>
    RedirectWithState(indexUrl, RedirectState(() => S.error(S ? message)))

  def RedirectToIndex = RedirectResponse(indexUrl)

  def RedirectToIndexWithCookies =
    RedirectResponse(indexUrl, S.responseCookies:_*)

  def RedirectToLoginWithReferrer = {
    val uri = S.uriAndQueryString
    RedirectWithState(loginUrl, RedirectState(() => { LoginRedirect.set(uri) }))
  }

  def HasPermission(permission: Permission) = If(
    () => loginManager.hasPermission(permission), () => RedirectToLoginWithReferrer
  )

  def HasAnyPermission(permissions: Permission*) = If({
    val ps = permissions.toList
    () => ps.exists(loginManager.hasPermission)}, () => RedirectToLoginWithReferrer
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

object LoginRedirect extends SessionVar[Box[String]](Empty) {
  override def __nameSalt = Helpers.nextFuncName
}
