package main.deathwatch

import akka.actor.{Terminated, Props, ActorLogging, Actor}
import main.protocols.QuoteRepositoryProtocol.QuoteRepositoryRequest
import main.protocols.TeacherProtocol.QuoteRequest

/**
  * @see http://rerun.me/2014/10/31/akka-notes-deathwatch-7/
  */
class QuoteActorWatcher extends Actor with ActorLogging {

  /*
    Creates a QuoteRepositoryActor as child, and watches over it using context.watch
   */
  val quoteRepositoryActor = context.actorOf(Props[QuoteRepositoryActor], "quoteRepoActor")
  context.watch(quoteRepositoryActor)

  def receive = {
    case QuoteRequest => quoteRepositoryActor ! QuoteRepositoryRequest
    case Terminated(terminatedActorRef) => log.error(s"Child Actor {$terminatedActorRef} Terminated")
  }

}
