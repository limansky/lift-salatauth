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

case class Permission(domain: String,
                      action: String = Permission.wildcardToken,
                      entity: String = Permission.wildcardToken) {

  def implies(p: Permission): Boolean = {
    p match {
      case Permission(Permission.wildcardToken, _, _) => true
      case Permission(this.domain, a, e) => {
        if (a == Permission.wildcardToken) {
          true
        } else {
          if (action == a) {
            if (e == Permission.wildcardToken) {
              true
            } else {
              entity == e
            }
          } else {
            false
          }
        }
      }
      case _ => false
    }
  }

  def implies(ps: Iterable[Permission]): Boolean = ps.exists(this.implies)
}


object Permission {
  val wildcardToken = "*"
  val all = Permission(wildcardToken)
}

case class Role(_id: String, comment: String, permissions: List[Permission])
