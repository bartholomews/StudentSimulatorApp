package main.supervision

import akka.actor.SupervisorStrategy.{Resume, Escalate, Stop, Restart}
import akka.actor._
import main.deathwatch.QuoteRepositoryActor
import main.protocols.QuoteRepositoryProtocol.QuoteRepositoryRequest
import main.protocols.TeacherProtocol.QuoteRequest
import scala.concurrent.duration._

/**
  * @see http://rerun.me/2014/11/10/akka-notes-actor-supervision-8/
  */
class TeacherActorOneForOne extends Actor with ActorLogging {

  val quoteRepositoryActor = context.actorOf(Props[QuoteRepositoryActor])


  /*
  The default 'supervisorStrategy' declared in the Actor trait;
   */
  /*override*/ val default = OneForOneStrategy() {
      /*
      The Actor could not be initialized;
      */
    case _: ActorInitializationException => Stop
      /*
      The Actor was killed using the 'Kill' message:
      its own and all its children's mailboxes are suspended,
      all its children are stopped and their watchers informed
      with a 'Terminated' message, all its own watchers informed
      as well, finally stopped.
      */
    case _: ActorKilledException => Stop
      /*
      A Supervisor did not handle its child's 'Terminated' message in its receive();
      The Supervisor stops that actor and the messages go into the queue of deadLetters.
       */
    case _: DeathPactException => Stop
      /*
      For all other Exceptions, the default Directive is to Restart the Actor.
       */
    case _: Exception => Restart
      // other types of Throwable will be escalated to parent Actor.
  }

  /*
  In a OneForOneStrategy, each child is treated separately, that is cases are applied
  just to the failing child(ren);
   */
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
        /*
        'Resume' just ignores the exception and proceeds
        processing the next message in the mailbox.
         */
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
        /*
        'Escalate' is for critical Exceptions, and the immediate Supervisor
        is not able to handle it, so it throws it up to the Top Level Actor.
         */
      case _: Exception                => Escalate
    }

  /*
  In a AllForOneStrategy, any decision is applied to all children of the Supervisor,
  not just the failing one.

   override val supervisorStrategy = AllForOneStrategy() {
   case _: Exception => Stop
  }
  */

  var requestCount = 0

  def receive = {
    case QuoteRequest => {
      if(requestCount <= 3) quoteRepositoryActor!QuoteRepositoryRequest
      else throw new MinorRecoverableException("ASDAS")
    }

  }

}