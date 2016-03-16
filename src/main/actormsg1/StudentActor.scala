package main.actormsg1

import akka.actor.{ActorLogging, Actor, ActorRef}
import main.protocols.StudentProtocol.InitSignal
import main.protocols.TeacherProtocol.{QuoteResponse, QuoteRequest}

/**
  * @see http://rerun.me/2014/10/06/akka-notes-actor-messaging-request-and-response-3/
  */
class StudentActor(teacherActorRef: ActorRef) extends Actor with ActorLogging {

  def receive = {

    case InitSignal => teacherActorRef!QuoteRequest

    case QuoteResponse(quoteString) =>
      log.info("Received QuoteResponse from Teacher")
      log.info(s"Printing from Student Actor $quoteString")

  }


}
