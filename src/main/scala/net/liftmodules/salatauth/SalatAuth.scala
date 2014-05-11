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

import net.liftweb.http.Factory
import com.mongodb.casbah.MongoCollection
import simple.SimpleLoginManager
import org.joda.time.ReadablePeriod
import org.joda.time.Days

/**
 * Stores parameter of SalatAuth module
 */
object SalatAuth extends Factory {

  /**
   * Path to index page.
   *
   * Default: "/"
   */
  val indexUrl = new FactoryMaker[String]("/") {}

  /**
   * Path to login page.
   *
   * Default: "/login"
   */
  val loginUrl = new FactoryMaker[String]("/login") {}

  /**
   * Path to logout page.
   *
   * Default: "/logout"
   */
  val logoutUrl = new FactoryMaker[String]("/logout") {}

  /**
   * Instance of MongoCollection to store roles
   */
  val rolesCollection = new FactoryMaker[Option[MongoCollection]](None) {}

  /**
   * Instance of MongoCollection to store session information
   */
  val sessionsCollection = new FactoryMaker[Option[MongoCollection]](None) {}

  /**
   * Session expiration period. Default: 30 days.
   */
  val sessionTtl = new FactoryMaker[ReadablePeriod](Days.days(30)) {}

  /**
   * Session cookie name. Default: SalatAuth.
   */
  val sessionCookieName = new FactoryMaker[String]("SalatAuth") {}

  /**
   * Session cookie path. Default: /.
   */
  val sessionCookiePath = new FactoryMaker[String]("/") {}

  /**
   * Session cookie domain.
   */
  val sessionCookieDomain = new FactoryMaker[Option[String]](None) {}

  /**
   * Instance of MongoCollection to store users if you are using SimpleLoginManager
   */
  val simpleCollection = new FactoryMaker[Option[MongoCollection]](None) {}

  /**
   * Instance of LoginManager to be used.
   *
   * Default: SimpleLoginManager
   */
  val loginManager = new FactoryMaker[LoginManager[_, _]](SimpleLoginManager) {}

  def init(
    indexUrl: String = "/",
    loginUrl: String = "/login",
    logoutUrl: String = "/logout",
    rolesCollection: Option[MongoCollection],
    sessionCollection: Option[MongoCollection],
    seesionTtl: ReadablePeriod,
    simpleCollection: Option[MongoCollection],
    loginManager: LoginManager[_, _]): Unit = {
    this.indexUrl.default.set(indexUrl)
    this.loginUrl.default.set(loginUrl)
    this.logoutUrl.default.set(logoutUrl)
    this.rolesCollection.default.set(rolesCollection)
    this.sessionsCollection.default.set(sessionCollection)
    this.sessionTtl.default.set(seesionTtl)
    this.simpleCollection.default.set(simpleCollection)
    this.loginManager.default.set(SimpleLoginManager)
  }
}
