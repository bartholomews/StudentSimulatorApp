package main.actorpath

import akka.actor.{Actor, Props, ActorSystem}
import main.actormsg1.TeacherActor
import main.lifecycle.BasicLifecycleLoggingActor

/**
  * @see http://rerun.me/2014/10/21/akka-notes-child-actors-and-path/
  */
object DriverPath extends App {

  val actorSystem = ActorSystem("SupervisionActorSystem")

  val actorRefAutoName = actorSystem.actorOf(Props[BasicLifecycleLoggingActor])
  println(actorRefAutoName.path)  // (prints) akka://SupervisionActorSystem/user/$a

  val actorRefCustomName = actorSystem.actorOf(Props[BasicLifecycleLoggingActor], "teacherActor")
  println(actorRefCustomName.path)  // (prints) akka://SupervisionActorSystem/user/teacherActor

  /*
    Actors Hierarchy:
    3. userGuardian Actor - root of all actors under /user
    (e.g. '/user/teacherSupervisor')
    2. systemGuardian Actor - root of all actors under /system [it shuts itself down when userGuardian is dead]
    (e.g. '/system/deadLetterListener', or all the akka.loggers Actors)
    1. rootGuardian Actor - root of both systemGuardian and userGuardian
    (i.e. '/')
   */

  class TeacherSupervisor extends Actor {

    /*
      Create a TeacherActor to be a child of TeacherSupervisor,
      using ActorContext.actorOf(Props[Actor-Class])
      instead of ActorSystem.actorOf(Props[Actor-Class])
      in the hierarchy, that is 'user/teacherSupervisor/teacherActor')
     */
    val teacherActor = context.actorOf(Props[TeacherActor], "teacherActor")

    def receive = {
      case "stop" => context.stop(self)
    }

  }





}
