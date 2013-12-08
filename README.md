# Lift SalatAuth module

   Authentication and authorization module for [Lift][Lift] web framework based on [Salat][Salat] library.  This project inspired by [lift-mongoauth][lift-mongoauth] module.  The goal is to avoid using different ORMs in project using Salat.

## Usage

  Lift SalatAuth provides several base classes and default implementations for them. You should choose if you want to implement them yourself or use default implementation.

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
) extends ProtoUser(username, password, email, roles)
```

 The `ProtoUser` class uses your roles collection to set permissions properly.  To make it work you have to set the collection during your application initialization:

```Scala
class Boot {
  def boot() {
    ...
    SalatAuth.rolesCollection.default.set(MongoConnection()("mydb")("roles"))
    ...
```

  If the default field set of `ProtoUser` is enough for you you can not define your own user class, but use `SimpleUser` instead.

### LoginManager

  Login manager is a core object of SalatAuth module.  You can use `SimpleLoginManager` with `SimpleUser`s or implement your own one for custom entities.  If you use "Simple" solution than you must define a collection in Boot:

```Scala
    SalatAuth.simpleCollection.default.set(MongoConnection()("mydb")("users"))
```

  Else, you need to define your LoginManager to find users in MongoDB:

```Scala
object MyLoginManager extends LoginManager[MyUser, ObjectId] {
    
  override def findUserById(id: ObjectId): Option[MyUser] = {
    MyUserDAO.findOneById(id)
  }

  override def getUserId(user: MyUser): ObjectId = user._id
}
```

Now, when you have a `LoginManager` instance you must set it in Boot (`SimpleLoginManager` is the default setting):

```Scala
    SalatAuth.loginManager.default.set(MyLoginManager)
```

### Defining SiteMap

[Lift]: http://liftweb.net
[Salat]: https://github.com/novus/salat
[lift-mongoauth]: https://github.com/eltimn/lift-mongoauth
