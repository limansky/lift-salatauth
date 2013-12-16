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
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._

object SimpleLoginManager extends LoginManager[SimpleUser, ObjectId] {

  lazy val collection = SalatAuth.simpleCollection.vend

  override def findUserById(id: ObjectId): Option[SimpleUser] = {
    collection.flatMap(_.findOneByID(id)).map(dbo => grater[SimpleUser].asObject(dbo))
  }

  override def getUserId(user: SimpleUser): ObjectId = user._id
}
