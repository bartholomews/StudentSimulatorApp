package main.protocols

/**
  * Protocol for the StudentActor
  * @see http://rerun.me/2014/10/06/akka-notes-actor-messaging-request-and-response-3/
  */
object StudentProtocol {
  case class InitSignal()
}
