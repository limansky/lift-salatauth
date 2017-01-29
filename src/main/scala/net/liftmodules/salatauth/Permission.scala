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

/**
 * Permissions are defied as triplets of domain, action and entity.  Domain is
 * the only mandatory part of permission.  All other parts by default replaced
 * with wildcard symbol (*). For example:
 * {{{
 *  Permission("printer") == Permission("printer", "*") == Permission("printer", "*", "*")
 * }}}
 *
 * The other examples:
 * {{{
 * Permission("goods", "view")
 * Permission("users", "edit", "123")
 * }}}
 */
case class Permission(
    domain: String,
    action: String = Permission.wildcardToken,
    entity: String = Permission.wildcardToken
) {

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

/**
 * User role entity class.
 *
 * It's up to you to store the roles into MongoDB. For example you can use
 * SalatDAO:
 * {{{
 *   object RoleDAO extends SalatDAO[Role, ObjectId](collection = MongoConnection()("mydb")("roles"))
 * }}}
 *
 * Also you need to define rolesCollection in your Boot class:
 * {{{
 *   SalatAuth.rolesCollection.default.set(myRoleCollection)
 * }}}
 *
 * @param _id the role name is the primary id for the collection
 * @param comment role description or other comment
 * @param permissions a set of permissions for this role
 */
case class Role(_id: String, comment: String, permissions: List[Permission])
