package module3

import module3.emailService.{Email, EmailService, Html}
import module3.userDAO.UserDAO
import zio.console.Console
import zio.macros.accessible
import zio.{Has, RIO, RLayer, ULayer, URLayer, ZIO, ZLayer}

package object userService {

  /**
   * Реализовать сервис с одним методом
   * notifyUser, принимает id пользователя в качестве аргумента и шлет ему уведомление
   * при реализации использовать UserDAO и EmailService
   */

   // 1
   type UserService = Has[UserService.Service]
   // 2
   @accessible
   object UserService{
     trait Service{
       def notifyUser(id: UserID): RIO[EmailService with UserDAO with Console, Unit]
     }

     class UserServiceImpl(userDAO: UserDAO.Service) extends Service{
       override def notifyUser(id: UserID): RIO[EmailService with Console, Unit] = for{
         user <- userDAO.findBy(UserID(1)).some.orElseFail(new Throwable(s"user not found with id - 1"))
         email = Email(user.email, Html("Hello here"))
         _ <- EmailService.sendEmail(email)
       } yield ()
     }

     val live: URLayer[UserDAO, UserService] = ZLayer.fromService[UserDAO.Service, UserService.Service](udao =>
       new UserServiceImpl(udao))
   }



}
