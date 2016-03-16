package test

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, EventFilter}
import com.typesafe.config.ConfigFactory
import main.actormsg1.{TeacherActor, StudentActor}
import main.protocols.StudentProtocol.InitSignal

import org.scalatest._

/**
  * @see http://rerun.me/2014/10/06/akka-notes-actor-messaging-request-and-response-3/
  */
class DriverAppTest extends TestKit(ActorSystem("UniversityMessageSystem",
  ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))

  with WordSpecLike with MustMatchers with BeforeAndAfterAll {

  "A student" must {

    "log a QuoteResponse eventually when an InitSignal is sent to it" in {

      val teacherRef = system.actorOf(Props[TeacherActor], "teacherActor")
      val studentRef = system.actorOf(Props(new StudentActor(teacherRef)), "studentActor")

      EventFilter.info (start="Printing from Student Actor", occurrences=1).intercept{
        studentRef!InitSignal
      }
    }
  }


}
