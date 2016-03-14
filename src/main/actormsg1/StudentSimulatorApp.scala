package main.actormsg1

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import main.protocols.TeacherProtocol._

/**
  *
  */
object StudentSimulatorApp extends App {

  /*
   * Initialize the ActorSystem.
   * ActorSystem is the entry point and root through which is possible to
   * create and top Actors, and shutdown the entire Actor environment;
   *
   */
  val actorSystem = ActorSystem("UniversityMessageSystem")

  /*
   * Construct the Teacher ActorRef.
   * The ActorRef acts as a Proxy for the actual Actors,
   * to avoid direct access to any custom/private methods or vars in Actor;
   * 'actorOf' is a method of ActorSystem to create an Actor.
   */
  val teacherActorRef = actorSystem.actorOf(Props[TeacherActor])

  /*
   * Send a message to the Teacher Actor.
   * The ActorRef delegates the message handling functionality to the Dispatcher;
   * After the ActorSystem and ActorRef was created,
   * a Dispatcher and Mailbox was created under the hood.
   *
   * A Dispatcher gets the messages from the ActorRef and passes it on to the Mailbox:
   * protected[akka] override def registerForExecution(mbox: Mailbox, ...): Boolean = {
   * ...
   * try {
   * 	executorService execute mbox
   * ...
   * }
   * The Dispatcher wraps an ExecutorService (ForkJoinPool or ThreadPoolExecutor)
   * and executes the Mailbox against the ExecutorService;
   *
   * A Mailbox is associated with each Actor, and has a FIFO MessageQueue of messages;
   * to store a process the messages; it is essentialy a Thread executed
   * by the Dispatcher's ExecutorService:
   * private[akka] abstract class Mailbox(val messageQueue: MessageQueue)
   * 														  extends SystemMessageQueue with Runnable
   * When the Mailbox's run() method gets fired, it dequeues a message and passes it
   * to the associated Actor for processing, and the receive() method of the target
   * Actor eventually should get called;
   */
  teacherActorRef!QuoteRequest


  // wait before shutting down the system
  Thread.sleep(2000)
  // shutdown() is @deprecated
  // actorSystem.shutdown()
  actorSystem.terminate()

}