package main.actormsg1

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import main.protocols.StudentProtocol.InitSignal
import main.protocols.TeacherProtocol._

/**
  * @see http://rerun.me/2014/10/06/akka-notes-actor-messaging-request-and-response-3/
  */
object DriverApp extends App {

  /*
   * 1. Initialize the ActorSystem.
   * ActorSystem is the entry point and root through which is possible to
   * create and top Actors, and shutdown the entire Actor environment;
   *
   */
  val actorSystem = ActorSystem("UniversityMessageSystem")

  /*
   * 2. Construct the Teacher ActorRef.
   * The ActorRef acts as a Proxy for the actual Actors,
   * to avoid direct access to any custom/private methods or vars in Actor;
   * 'actorOf' is a method of ActorSystem to create an Actor.
   */
  val teacherActorRef = actorSystem.actorOf(Props[TeacherActor])


  /**
    * 3. Construct the Student ActorRef, passing the ActorRef of the TeacherActor
    * as a constructor parameter,so that the StudentActor could use the ActorRef
    * for sending messages to the TeacherActor. It could be done with other ways,
    * like passing in the Props or using child actors.
    */
  val studentActorRef = actorSystem.actorOf(Props(new StudentActor(teacherActorRef)), "studentActor")

  /**
    * 4. Send the InitSignal (a StudentProtocol case class) to the StudentActorRef,
    * so that it could start sending QuoteRequest message to the TeacherActor,
    * as case class in receive() method in StudentActor;
    */
  studentActorRef!InitSignal

  // wait before shutting down the system
  Thread.sleep(2000)
  // shutdown() is @deprecated
  // actorSystem.shutdown()
  actorSystem.terminate()

}