# Lift SalatAuth module

   Authentication and authorization module for [Lift][Lift] web framework based on [Salat][Salat] library.  This project inspired by [lift-mongoauth][lift-mongoauth] module.  Currently it doesn't support "remember me" functionality, but I'm going to add it in the next release.  The goal is to avoid using different ORMs in project using Salat.

## Installing

  The module is available in Sonatype repository. So, if you using sbt just add it to libraryDependencies.

For Lift 2.5.x:

```
"net.liftmodules" %% "salatauth_2.5" % "1.0"

```
For Lift 2.6.x:

```
"net.liftmodules" %% "salatauth_2.6" % "1.0"

```
For Lift 3.0.x:

```
"net.liftmodules" %% "salatauth_3.0" % "1.0"

```

  Or you can use current snapshot version:
  [![Build Status](https://travis-ci.org/limansky/lift-salatauth.svg?branch=master)](https://travis-ci.org/limansky/lift-salatauth)

```
"net.liftmodules" %% "salatauth_2.6" % "1.1-SNAPSHOT"

```

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
    SalatAuth.rolesCollection.default.set(Some(MongoConnection()("mydb")("roles")))
    ...
```

  If the default field set of `ProtoUser` is enough for you you can not define your own user class, but use `SimpleUser` instead.

### Permissions model

Each user has a list of roles defined as strings.  However role itself is a case class.  The role name is an `_id` for a role.  As it was described you need to set roles collection in boot to allow roles works properly.  Roles contains a list of `Permission`s.  Permissions are defined as a triplets of domain, action and entity.  

```Scala
val adminRole = Role("admin", "", List(Permission.All))
val userRole = Role("user", "", List(Permission("Messages"), Permission("Reports", "view"), Permission("Reports", "print"), Permission("Profile", "view")))

RolesDAO.save(adminRole)
RolesDAO.save(userRole)

val user = SimpleUser("admin", ProtoUser.hashPassword("secret"), "admin@example.com", Set("admin"))
UserDAO.save(user)
```

### LoginManager

  Login manager is a core object of SalatAuth module.  You can use `SimpleLoginManager` with `SimpleUser`s or implement your own one for custom entities.  If you use "Simple" solution than you must define a collection in Boot:

```Scala
    SalatAuth.simpleCollection.default.set(Some(MongoConnection()("mydb")("users")))
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

  There are several methods defined in trait `Locs` helps you to set required permissions on the entries in site map. Here is the example:
```Scala
import net.liftmodules.salatauth.Locs._

def siteMap() = SiteMap(
  Menu.i("index") / "index" >> Hidden,
  Menu.i("messages") / "messages" >> RequireLoggedIn,
  Menu.i("users") / "users" >> HasRole("admin"),
  Menu.i("reports") / "reports" >> HasPermission("Reports" : "view")
  buildLogin("login", Hidden :: Nil),
  buildLogout("logout", Nil)
  )
```

In this example user has to be logged in for all pages except index, but to see users he must have "admin" role, and to see the reports he must have corresponding permission.

### Session handling

  Session support was added in 1.1-SNAPSHOT.  This feature allows you to add "remember me" functionality.  To do it you need to setup cookie settings you your Boot.scala, and add session check hook:
```Scala
SalatAuth.loginManager.default.set(MyLoginManager)
SalatAuth.sessionCookieName.default.set("MyAppLoginCookie")
SalatAuth.sessionsCollection.default.set(Some(mongoDB("authcookies")))
LiftRules.earlyInStateful.append(MyLoginManager.checkSession)
```

Now you can pass additional parameter to LoginManager.logUserIn function to set if user was authorized and shall the login cookie be created.

[Lift]: http://liftweb.net
[Salat]: https://github.com/novus/salat
[lift-mongoauth]: https://github.com/eltimn/lift-mongoauth
