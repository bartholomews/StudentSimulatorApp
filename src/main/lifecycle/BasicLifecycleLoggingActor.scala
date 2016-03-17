package main.lifecycle

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive

/**
  * @see http://rerun.me/2014/10/21/akka-notes-actor-lifecycle-basic/
  */
class BasicLifecycleLoggingActor extends Actor with ActorLogging {

  log.info("Inside BasicLifecycleLoggingActor Constructor")

  // ActorRef <- ActorContext.context.self
  /* (unlike Servlets, Actors have access to the ActorContext
      even inside the constructor) */
  log.info (context.self.toString())

  override def preStart() = {
    log.info("Inside the preStart method of BasicLifecycleLoggingActor")
  }

  def receive = LoggingReceive {
    case "hello" => log.info("hello")
    case "stop" => context.stop(self)
  }

  /*
    The postStop gets called after the ActorSystem shutdown/terminate,
    or directly from ActorSystem.stop("name"),
    or by way of a message, as in actorName!"stop"
    with receive case "stop" => ActorContext.context.stop(self),
    or a direct:
    actorName!PoisonPill (a Terminated message is sent to all watchers)
    actorName!Kill (an ActorKilledException is thrown by the host Actor
                    which gets propagated to its Supervisor)
   */
  override def postStop() = {
    log.info("Inside postStop method of BasicLifecycleLoggingActor")
  }



}
