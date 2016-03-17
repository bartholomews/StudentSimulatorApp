package main.protocols

/**
  * @see http://rerun.me/2014/10/31/akka-notes-deathwatch-7/
  */
object QuoteRepositoryProtocol {
  case class QuoteRepositoryRequest()
  case class QuoteRepositoryResponse(quote: String)
}
