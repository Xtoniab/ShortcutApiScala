package encoders

import io.circe.{Encoder, Json}

object Encoders {
  implicit val booleanOrStringEncoder: Encoder[Boolean | String] = Encoder.instance {
    case b: Boolean => Json.fromBoolean(b)
    case s: String  => Json.fromString(s)
  }

  implicit val mapEncoder: Encoder[Map[String, Boolean | String]] = Encoder.encodeMap[String, Boolean | String]
}
