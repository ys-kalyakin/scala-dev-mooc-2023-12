package module4.homework.dao.repository

import zio.{Has, ULayer, ZIO, ZLayer}
import io.getquill.context.ZioJdbc._
import module4.homework.dao.entity.User
import zio.macros.accessible
import module4.homework.dao.entity.{Role, UserToRole}
import module4.homework.dao.entity.UserId
import module4.homework.dao.entity.RoleCode
import module4.phoneBook.db

import java.sql.SQLException
import javax.sql.DataSource


object UserRepository{


    val dc = db.Ctx
    import dc._

    type UserRepository = Has[Service]

    trait Service{
        def findUser(userId: UserId): QIO[Option[User]]
        def createUser(user: User): QIO[User]
        def createUsers(users: List[User]): QIO[List[User]]
        def updateUser(user: User): QIO[Unit]
        def deleteUser(user: User): QIO[Unit]
        def findByLastName(lastName: String): QIO[List[User]]
        def list(): QIO[List[User]]
        def userRoles(userId: UserId): QIO[List[Role]]
        def insertRoleToUser(roleCode: RoleCode, userId: UserId): QIO[Unit]
        def listUsersWithRole(roleCode: RoleCode): QIO[List[User]]
        def findRoleByCode(roleCode: RoleCode): QIO[Option[Role]]
    }

    class ServiceImpl extends Service{
        private lazy val userSchema = quote {
            querySchema[User]("User")
        }

        private lazy val roleSchema = quote {
            querySchema[Role]("Role")
        }

        private lazy val userToRoleSchema = quote {
            querySchema[UserToRole]("UserToRole")
        }

        override def findUser(userId: UserId): QIO[Option[User]] = 
            run(userSchema.filter(_.id == lift(userId.id))).map(_.headOption)

        override def createUser(user: User): QIO[User] = run(userSchema.insert(lift(user))).as(user)

        override def createUsers(users: List[User]): QIO[List[User]] =
            run(quote {
                liftQuery(users).foreach { u => userSchema.insert(u) }
            }).as(users)

        override def updateUser(user: User): QIO[Unit] =
            run(quote {userSchema.filter(_.id == lift(user.id))}.update(lift(user))).unit

        override def deleteUser(user: User): QIO[Unit] =
            run(userSchema.filter {_.id == lift(user.id)}.delete).unit

        override def findByLastName(lastName: String): QIO[List[User]] =
            run(userSchema.filter{ _.lastName == lift(lastName) })

        override def list(): QIO[List[User]] = run(userSchema)

        override def userRoles(userId: UserId): QIO[List[Role]] = {
            val query = quote {
               for {
                   user <- userSchema.filter { _.id == lift(userId.id) }
                   link <- userToRoleSchema if user.id == link.userId
                   role <- roleSchema if link.roleId == role.code
                } yield role
            }
            run(query)
        }

        override def insertRoleToUser(roleCode: RoleCode, userId: UserId): QIO[Unit] = 
            run(userToRoleSchema.insert(_.roleId -> lift(roleCode.code), _.userId -> lift(userId.id))).unit

        override def listUsersWithRole(roleCode: RoleCode): QIO[List[User]] = {
            val q = quote {
                for {
                    users <- userSchema
                    link <- userToRoleSchema.join(l => l.userId == users.id)
                    role <- roleSchema.filter {_.code == lift(roleCode.code)}.join(r => r.code == link.roleId)
                } yield (users, link, role)._1
            }
        
            run(q.distinct)
        }

        override def findRoleByCode(roleCode: RoleCode): QIO[Option[Role]] = 
            run(roleSchema.filter {_.code == lift(roleCode.code)}).map(_.headOption)
    }

    val live: ULayer[UserRepository] = ZLayer.succeed(new ServiceImpl)
}