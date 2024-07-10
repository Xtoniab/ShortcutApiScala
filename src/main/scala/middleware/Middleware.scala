package middleware

import org.http4s.{HttpRoutes, Request}
import cats.data.Kleisli
import cats.effect.IO
import org.typelevel.ci.CIStringSyntax

object Middleware {

  def stripPrefix(routes: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
    req.headers.get(ci"X-Forwarded-Prefix") match {
      case Some(header) =>
        val prefix = header.head.value
        val path = req.uri.path.toString
        if (path.startsWith(prefix)) {
          val newPath = path.substring(prefix.length)
          val newUri = req.uri.withPath(org.http4s.Uri.Path.unsafeFromString(newPath))
          val newReq = req.withUri(newUri)
          routes(newReq)
        } else {
          routes(req)
        }
      case None =>
        routes(req)
    }
  }
}
