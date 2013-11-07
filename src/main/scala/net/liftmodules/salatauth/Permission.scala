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
