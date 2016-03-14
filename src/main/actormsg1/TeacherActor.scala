package main.actormsg1

import scala.util.Random

import akka.actor.{ActorLogging, Actor}
import main.protocols.TeacherProtocol._

/**
  * This class should use ActorLogging
  * which uses the EventBus of the Actor framework
  * instead of plain old system.out
  *
  * @see http://rerun.me/2014/09/19/akka-notes-actor-messaging-1/
  */
class TeacherActor extends Actor {
  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  def receive = {
    case QuoteRequest =>

      // Get a random Quote from the list and construct a response
      val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))

      println(quoteResponse)

  }

  /*
  This method is used in test.TeacherPreTest to give access
  to quotes List and test its size;
  */  def quoteList=quotes

}