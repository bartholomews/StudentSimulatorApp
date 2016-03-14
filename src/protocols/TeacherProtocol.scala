package protocols

/**
  * Protocol for the TeacherActor
  * @see http://rerun.me/2014/09/19/akka-notes-actor-messaging-1/
  */
object TeacherProtocol {
  case class QuoteRequest()
  case class QuoteResponse(quoteString: String)
}