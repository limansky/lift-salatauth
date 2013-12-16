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

object SalatAuth extends Factory {

  val indexUrl = new FactoryMaker[String]("/") {}
  val loginUrl = new FactoryMaker[String]("/login") {}
  val logoutUrl = new FactoryMaker[String]("/logout") {}
  val rolesCollection = new FactoryMaker[Option[MongoCollection]](None) {}
  val simpleCollection = new FactoryMaker[Option[MongoCollection]](None) {}
  val loginManager = new FactoryMaker[LoginManager[_, _]](SimpleLoginManager) {}

  def init(
    indexUrl: String = "/",
    loginUrl: String = "/login",
    logoutUrl: String = "/logout",
    rolesCollection: Option[MongoCollection],
    simpleCollection: Option[MongoCollection],
    loginManager: LoginManager[_, _]): Unit = {
    this.indexUrl.default.set(indexUrl)
    this.loginUrl.default.set(loginUrl)
    this.logoutUrl.default.set(logoutUrl)
    this.rolesCollection.default.set(rolesCollection)
    this.simpleCollection.default.set(simpleCollection)
    this.loginManager.default.set(SimpleLoginManager)
  }
}
