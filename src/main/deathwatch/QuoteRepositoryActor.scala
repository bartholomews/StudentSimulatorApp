package main.deathwatch

import akka.actor.{PoisonPill, ActorLogging, Actor}
import main.protocols.QuoteRepositoryProtocol.{QuoteRepositoryResponse, QuoteRepositoryRequest}

import scala.util.Random

/**
  * @see http://rerun.me/2014/10/31/akka-notes-deathwatch-7/
  */
class QuoteRepositoryActor extends Actor with ActorLogging {

  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  var repoRequestCount:Int = 1

  def receive = {
    case QuoteRepositoryRequest =>
      if(repoRequestCount>3) self!PoisonPill
      else {
        val quoteResponse = QuoteRepositoryResponse(quotes(Random.nextInt(quotes.size)))
        log.info("QuoteRequest received in QuoteRepositoryActor. " +
          s"Sending response to Teacher Actor $quoteResponse")
        repoRequestCount = repoRequestCount+1
        sender ! quoteResponse
      }
  }

}
