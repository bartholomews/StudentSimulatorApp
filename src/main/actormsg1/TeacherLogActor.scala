package main.actormsg1

import scala.util.Random

import akka.actor.{ActorLogging, Actor}
import main.protocols.TeacherProtocol._

/**
  * This class should use ActorLogging
  * which uses the EventBus of the Actor framework
  * instead of plain old system.out
  *
  * @see http://rerun.me/2014/09/29/akka-notes-logging-and-testing/
  */
class TeacherLogActor extends Actor with ActorLogging {
  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  def receive = {
    case QuoteRequest =>

      /*
      Get a random Quote from the list and construct a response
       */
      val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))

      // println(quoteResponse)

      /*
      The logging methods in the ActorLogging publishes
      the log messages to an akka.event.EventStream:
      EventStream is a stream of events (both system and user generated)
      where subscribers are ActorRefs, channels are Classes
      and Events are any java.lang.Object.
      By default, the Actor that subscribes to these messages
      is the akka.event.logging.DefaultLogger
      which simply prints the message to standard out:
       *
       * class DefaultLogger extends Actor with StdOutLogger {
       *   override def receive: Receive = {
       *    ...
       *    case event: LogEvent => print(event)
       *   }
       * }
       *
      */
      log.info(quoteResponse.toString)

  }

  /*
  This method is used in test.TeacherPreTest to give access
  to quotes List and test its size;
  */
  def quoteList=quotes

}