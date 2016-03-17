package main.lifecycle

import akka.actor._

/**
  * @see http://rerun.me/2014/10/21/akka-notes-actor-lifecycle-basic/
  */
object LifecycleApp extends App {

  val actorSystem = ActorSystem("LifecycleActorSystem")
  val lifecycleActor = actorSystem.actorOf(Props[BasicLifecycleLoggingActor], "lifecycleActor")

  lifecycleActor ! "hello"
  /*
    BasicLifecycleLoggingActor in its receive method has
    case "stop" => context.stop(self)
   */
  lifecycleActor ! "stop"

  /*
    A Terminated message is sent to all watchers
   */
  // lifecycleActor ! PoisonPill

  /*
    An ActorKilledException is thrown by the host Actor
    and gets propagated to its Supervisor
   */

  //lifecycleActor ! Kill

  /*
    Any message sent to an Actor that is terminated gets forwarded
    to the mailbox of an internal Actor called DeadLetterActor:
    the DeadLetterActor processes the message in its mailbox,
    wraps each message as a DeadLetter and published it to the EventStream.
    One other Actor called DeadLetterListener consumes all DeadLetter instances
    and publishes that as a log message.
   */
  lifecycleActor ! "hello"

  /*
    Create an Actor which prints to standard out DeadLetter received,
    and subscribe it to the EventStream's DeadLetter channel
   */
  val deadLetterListener = actorSystem.actorOf(Props[MyCustomDeadLetterListener])
  actorSystem.eventStream.subscribe(deadLetterListener, classOf[DeadLetter])

  // wait for a couple of seconds before terminate
  Thread.sleep(2000)
  actorSystem.terminate()

}

class MyCustomDeadLetterListener extends Actor {
  def receive = {
    case deadLetter: DeadLetter => println(s"FROM CUSTOM LISTENER $deadLetter")
  }
}
