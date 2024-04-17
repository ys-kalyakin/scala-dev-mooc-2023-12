package catsmiddleware

import cats.Functor
import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.Ref
import cats.effect.{IO, IOApp, Resource}
import org.http4s.{AuthedRequest, AuthedRoutes, EmptyBody, Http, HttpRoutes, Method, Request, Status, Uri}
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.{Host, Port}
import org.http4s.server.{AuthMiddleware, Router}
import org.typelevel.ci.CIStringSyntax
import cats.implicits.toSemigroupKOps

object Restfull {
  //1
  val service: HttpRoutes[IO] =
    HttpRoutes.of {
      case GET -> Root / "hello" /name => Ok("bla bla bla")
    }

  val serviceOne: HttpRoutes[IO] =
    HttpRoutes.of {
      case GET -> Root / "hello1" / name => Ok("bla1 bla1 bla1")
    }

  val serviceTwo: HttpRoutes[IO] =
    HttpRoutes.of {
      case GET -> Root / "hello2" / name => Ok("bla2 bla2 bla2")
    }

  val routes = Router("/" -> serviceOne, "/api" -> serviceTwo, "/apiroot" -> service)
  val httpApp: Http[IO,IO] = routes.orNotFound

  val server = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(httpApp).build
  } yield  s

  //2 middleware
  val routes2 = addResponseMiddleware(Router("/" -> addResponseMiddleware(serviceOne),
    "/api" -> addResponseMiddleware(serviceTwo),
    "/apiroot" -> addResponseMiddleware(service)))

  def addResponseMiddleware[F[_]: Functor](routes: HttpRoutes[F]): HttpRoutes[F] = Kleisli {
    req =>
      val maybeResponse = routes(req)
//      maybeResponse.map(resp=> resp.putHeaders("X-Outus" -> "Hello"))
      maybeResponse.map {
        case Status.Successful(resp) => resp.putHeaders("X-Otus" -> "Hello")
        case other => other
      }
  }
  val httpApp2: Http[IO,IO] = routes2.orNotFound


  val server2 = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(httpApp2).build
  } yield  s

  //3 sessions
  type Sessions[F[_]] = Ref[F, Set[String]]
  def serviceSessions(sessions: Sessions[IO]): HttpRoutes[IO] =
    HttpRoutes.of {
      case r@GET -> Root / "hello" =>
        r.headers.get(ci"X-User-Name") match {
          case Some(values) =>
            val name = values.head.value
            sessions.get.flatMap(users =>
            if (users.contains(name)) Ok(s"Hello, $name")
            else Forbidden("no access")
            )
          case None => Forbidden("no access")
        }
      case PUT -> Root / "login" / name =>
        sessions.update(set => set + name).flatMap(_ => Ok("done"))
    }

  def routerSessions(sessions: Sessions[IO]): HttpRoutes[IO] =
    addResponseMiddleware(Router("/" -> serviceSessions(sessions)))

  val serverSessionsServer = for {
    sessions <- Resource.eval(Ref.of[IO, Set[String]](Set.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(routerSessions(sessions).orNotFound).build
  } yield s

  // auth
  def routerSessionAuth(sessions: Sessions[IO]): HttpRoutes[IO] = {
    // <+> combine
  addResponseMiddleware(Router("/" -> (loginService(sessions) <+> serviceAuthMiddleware(sessions)(serviceHelloAuth))))
  }

  def loginService(sessions: Sessions[IO]): HttpRoutes[IO] =
    HttpRoutes.of {
      case PUT -> Root/ "login" / name =>
        sessions.update(set => set + name).flatMap(_=>Ok("done"))
    }

  def serviceHelloAuth: AuthedRoutes[User, IO] = AuthedRoutes.of {
    case GET -> Root / "hello" as user =>
      Ok(s"Hello, ${user.name}")
  }

  final case class User(name: String)
  def serviceAuthMiddleware(sessions: Sessions[IO]): AuthMiddleware[IO, User] =
    autherRoutes =>
      Kleisli{ req =>
        req.headers.get(ci"X-User-Name")  match {
          case Some(values) =>
            val name = values.head.value
            for {
              users <- OptionT.liftF(sessions.get)
              results <-
                if (users.contains(name)) autherRoutes(AuthedRequest(User(name), req))
                else OptionT.liftF(Forbidden("no access"))
            } yield results
          case None => OptionT.liftF(Forbidden("no access"))
        }
      }
  val serverSessionsAuthServer = for {
    sessions <- Resource.eval(Ref.of[IO, Set[String]](Set.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(routerSessionAuth(sessions).orNotFound).build
  } yield s

  //clear way
  def routerSessionsAuthClear(sessions: Sessions[IO]): HttpRoutes[IO] =
    addResponseMiddleware(Router("/" -> (loginService(sessions) <+> serviceAuthMiddleware(sessions)(serviceHelloAuth))))

  val sesverSessionAuthServerClear = for {
    sessions <- Resource.eval(Ref.of[IO, Set[String]](Set.empty))
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(routerSessionsAuthClear(sessions).orNotFound).build
  } yield s


}

object mainServer extends IOApp.Simple {
  def run(): IO[Unit] = {
    //Restfull.server.use(_ => IO.never)
    //Restfull.server2.use(_ => IO.never)
    //Restfull.serverSessionsServer.use(_ => IO.never)
    //Restfull.serverSessionsAuthServer.use(_ => IO.never)
    Restfull.sesverSessionAuthServerClear.use(_ => IO.never)
  }
}

//tests
object Test extends IOApp.Simple {
  def run: IO[Unit] = {
    val service = Restfull.serviceHelloAuth

    for {
      result <- service(AuthedRequest(Restfull.User("sjhdb"), Request(method = Method.GET,
        uri = Uri.fromString("/hello").toOption.get))).value
      _ <- result match {
        case Some(resp) =>
          resp.bodyText.compile.last.flatMap(body => IO.println(resp.status.isSuccess) *>
          IO.println(body))
        case None => IO.println("fail")
      }
    } yield()

  }
}