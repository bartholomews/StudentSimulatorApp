package main.actormsg1

import akka.actor.{ActorLogging, Actor, ActorRef}
import main.protocols.StudentProtocol.InitSignal
import main.protocols.TeacherProtocol.{QuoteResponse, QuoteRequest}
import scala.concurrent.duration._

/**
  * @see http://rerun.me/2014/10/06/akka-notes-actorsystem-in-progress/
  */
class StudentActorDelayed(teacherActorRef: ActorRef) extends Actor with ActorLogging {

  /**
    * StudentActor sends message to TeacherActor only after 5 seconds
    * of it receiving the InitSignal, and not immediately;
    * this is done by calling the scheduler method in ActorSystem,
    * which has a variety of schedule methods, e.g. scheduleOnce()
    */
  def receive = {
    case InitSignal => // teacherActorRef!QuoteRequest

      /*
      The import is necessary because the schedule methods require
      the implicit parameter ExecutionContext:

        final def schedule(
          initialDelay: FiniteDuration,
          interval: FiniteDuration,
          receiver: ActorRef,
          message: Any) (implicit executor: ExecutionContext,
                         sender: ActorRef = Actor.noSender): Cancellable =
         schedule(initialDelay, interval, new Runnable {
            def run = {
              receiver ! message
              if (receiver.isTerminated)
                throw new SchedulerException("timer active for terminated actor")
           }
         })

       The schedule method wraps the 'tell' in a 'Runnable' which is executed
       by the ExecutionContext that is passed in:

       From ActorCell.scala (Context)
       /**
         * Returns the dispatcher (MessageDispatcher) that is used for this Actor.
         * Importing this member will place an implicit ExecutionContext in scope.
         */
        implicit def dispatcher: ExecutionContextExecutor
       */
      import context.dispatcher

      /*
      ActorContent.context
      Actorsystem.system.scheduler
        Scheduler.scheduleOnce(delay: FiniteDuration, receiver: ActorRef, message: Any)
        context.system.scheduler.scheduleOnce(5.seconds, teacherActorRef, QuoteRequest)
      */

      /* Scheduler.schedule(initialDelay: FiniteDuration, interval: FiniteDuration,
                            receiver: ActorRef, message: Any) */
      context.system.scheduler.schedule(0.seconds, 5.seconds, teacherActorRef, QuoteRequest)

    case QuoteResponse(quoteString) =>
      log.info("Received QuoteResponse from Teacher")
      log.info(s"Printing from Student Actor $quoteString")

  }

}
