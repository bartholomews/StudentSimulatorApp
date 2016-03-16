package test

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, EventFilter, TestKit}
import com.typesafe.config.ConfigFactory
import main.actormsg1.{StudentActorDelayed, TeacherActor}
import main.protocols.StudentProtocol.InitSignal
import org.scalatest.{WordSpecLike, MustMatchers, BeforeAndAfterAll}

/**
  * @see http://rerun.me/2014/10/06/akka-notes-actorsystem-in-progress/
  */
class StudentActorDelayedTest extends TestKit(ActorSystem("UniversityMessageSystem",
  // ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
  ConfigFactory.parseString("""
    akka {
      loggers = ["akka.testkit.TestEventListener"]
      test {
        filter-leeway = 6s
      }
    } """)))

  with WordSpecLike with MustMatchers with BeforeAndAfterAll with ImplicitSender {

  "A delayed student" must {

  "fire the QuoteRequest after 5 seconds when an InitSignal is sent to it" in {

      val teacherRef = system.actorOf(Props[TeacherActor], "teacherActorDelayed")
      val studentRef = system.actorOf(Props(new StudentActorDelayed(teacherRef)), "studentDelayedActor")

    // the default timeout for the EventFilter to wait for the message to appear
    // in the EventStream is 3 seconds, needs to change the configuration property above.
      EventFilter.info (start="Printing from Student Actor", occurrences=1).intercept{
        studentRef!InitSignal
      }
    }

  }

  override def afterAll() {
    super.afterAll()
    system.terminate()
  }

}
