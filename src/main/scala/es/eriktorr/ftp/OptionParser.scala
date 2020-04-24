package es.eriktorr.ftp

import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

trait OptionParser extends LazyLogging {
  type OptionMap = Map[Symbol, String]

  def optionsFrom(args: Array[String]): OptionMap = {
    @tailrec
    def optionMap(parsedOptions: OptionMap, argsList: List[String]): OptionMap =
      argsList match {
        case "-config" :: value :: tail =>
          optionMap(parsedOptions ++ Map(Symbol("config") -> value), tail)
        case ::(value, next) =>
          logger.warn(s"Unknown option ignored: $value")
          optionMap(parsedOptions, next)
        case Nil => parsedOptions
      }
    optionMap(Map(), args.toList)
  }
}
