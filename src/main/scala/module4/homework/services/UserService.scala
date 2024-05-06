package module4.homework.services

import zio.Has
import zio.Task
import module4.homework.dao.entity.{Role, RoleCode, User, UserId, UserToRole}
import module4.homework.dao.repository.UserRepository
import zio.ZIO
import zio.RIO
import zio.ZLayer
import zio.macros.accessible
import module4.phoneBook.db
import module4.phoneBook.db.DataSource

@accessible
object UserService{
    type UserService = Has[Service]

    trait Service{
        def listUsers(): RIO[db.DataSource, List[User]]
        def listUsersDTO(): RIO[db.DataSource, List[UserDTO]]
        def addUserWithRole(user: User, roleCode: RoleCode): RIO[db.DataSource, UserDTO]
        def listUsersWithRole(roleCode: RoleCode): RIO[db.DataSource, List[UserDTO]]
    }

    class Impl(userRepo: UserRepository.Service) extends Service{
        val dc = db.Ctx
        import dc._

        def listUsers(): RIO[db.DataSource, List[User]] = userRepo.list()


        def listUsersDTO(): RIO[db.DataSource,List[UserDTO]] =
            for {
                users <- listUsers()
                dtos <- ZIO.foreach(users) { user2UserDTO }
            } yield dtos
        
        def addUserWithRole(user: User, roleCode: RoleCode): RIO[db.DataSource, UserDTO] = 
            dc.transaction {
                for {
                    user <- userRepo.createUser(user)
                    _ <- userRepo.insertRoleToUser(roleCode, UserId(user.id))
                    dto <- user2UserDTO(user)
                } yield dto
            }

        def listUsersWithRole(roleCode: RoleCode): RIO[db.DataSource,List[UserDTO]] = 
            for {
                users <- userRepo.listUsersWithRole(roleCode)
                dtos <- ZIO.foreach(users) {user2UserDTO }
            } yield dtos
        

        private def user2UserDTO(u: User) =
            userRepo.userRoles(UserId(u.id)).map {roles => UserDTO(u, roles.toSet) }
    }

    val live: ZLayer[UserRepository.UserRepository, Nothing, UserService] = ???
}

case class UserDTO(user: User, roles: Set[Role])