package main.supervision

/**
  * @see http://rerun.me/2014/11/10/akka-notes-actor-supervision-8/
  */
class MinorRecoverableException(msg: String) extends Throwable(msg)

