# Lift SalatAuth module

   Authentication and authorization module for [Lift][Lift] web framework based on [Salat][Salat] library.  This project inspired by [lift-mongoauth][lift-mongoauth] module.  The goal is to avoid using different ORMs in project using Salat.

## Usage

  Lift SalatAuth provides several base classes, which shall be implemented before using.

### User entity

  The user entity can be implemented as a case class extending ProtoUser.

```Scala
case class User(
  val _id: ObjectId,
  override val username: String,
  override val password: String,
  val realName: String,
  override val email: String,
  override val roles: Set[String]
) extends ProtoUser(username, password, email, roles {
  override def findRoles(query: DBObject) = RolesDAO.find(query).toList
}
```

  In this example __RolesDAO__ class is an implementation of SalatDAO for roles, also needed to be implemented, to define where the roles instance shall be stored.

```Scala
object RoleDAO extends SalatDAO[Role, ObjectId](collection = MongoConnection()("mydb")("roles"))
```

### Defining SiteMap

[Lift]: http://liftweb.net
[Salat]: https://github.com/novus/salat
[lift-mongoauth]: https://github.com/eltimn/lift-mongoauth
