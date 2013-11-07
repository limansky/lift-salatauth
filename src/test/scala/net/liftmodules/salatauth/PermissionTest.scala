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

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class PermissionTest extends FlatSpec with ShouldMatchers {

  "Permissions" should "handle only domain" in {

    Permission("test").implies(Permission.all) should be (true)
    Permission("test").implies(Permission("test")) should be (true)
    Permission("test").implies(Permission("toast")) should be (false)
  }

  it should "handle action value" in {
    val p = Permission("employee", "view")

    p.implies(Permission("employee", "edit")) should be (false)
    p.implies(Permission("employee", "view")) should be (true)
    p.implies(Permission("employee")) should be (true)
    p.implies(Permission("user")) should be (false)
    p.implies(Permission("user", "view")) should be (false)
    p.implies(Permission.all) should be (true)
  }

  it should "handle entity value" in {
    val p = Permission("user", "view", "user31")

    p.implies(Permission("user", "view", "user33")) should be (false)
    p.implies(Permission("user", "view", "user31")) should be (true)
    p.implies(Permission("state", "view", "user31")) should be (false)
    p.implies(Permission("user", "edit", "user31")) should be (false)
    p.implies(Permission("user", "view")) should be (true)
    p.implies(Permission("user")) should be (true)
    p.implies(Permission.all) should be (true)
  }

}
