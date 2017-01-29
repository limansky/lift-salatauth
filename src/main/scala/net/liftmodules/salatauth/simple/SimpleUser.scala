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
package simple

import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._

case class SimpleUser(
  val _id: ObjectId = ObjectId.get,
  override val username: String,
  override val password: String,
  val email: String,
  override val roles: Set[String]
) extends ProtoUser(username, password, roles)

object SimpleUser {
  def apply(username: String, password: String, email: String, roles: Set[String]): SimpleUser =
    SimpleUser(ObjectId.get, username, password, email, roles)
}
