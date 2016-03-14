package test

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import main.actormsg1.{TeacherLogParameterActor, TeacherLogActor, TeacherActor}
import main.protocols.TeacherProtocol.QuoteRequest

// internally, the TestKit decorates the ActorSystem
                            // and replaces the default dispatcher, too.
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

/**
  * @see http://rerun.me/2014/09/29/akka-notes-logging-and-testing/
  *
  * Actors are created via the TestActorRef[ActorName]
  * and not via system.actorOf, so that it is possible
  * to get access to the Actor's internals through the
  * TestActorRef.underlyingActor method;
  * for production code, ActorRef should be used instead.
  *
  */                                       // subscribe to the EventStream (where all the log messages go to) to assert the presence of the log message itself.
class TeacherPreTest extends TestKit(ActorSystem("UniversityMessageSystem", ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
with WordSpecLike // http://doc.scalatest.org/2.0/index.html#org.scalatest.WordSpecLike
with MustMatchers // http://doc.scalatest.org/2.0/index.html#org.scalatest.matchers.MustMatchers
with BeforeAndAfterAll {
  // http://doc.scalatest.org/2.0/index.html#org.scalatest.BeforeAndAfterAll

  /*
  1. Sends message to the Print Actor. Not really a test case,
  as it doesn't assert anything.
  */
  "A teacher" must {
    "print a quote when a QuoteRequest message is sent" in {
      val teacherRef = TestActorRef[TeacherActor]
      teacherRef ! QuoteRequest
    }
  }

  /*
  2. Sends message to the Log Actor, which uses the 'log' field
  of the ActorLogging to publish the message to the EventStream.
  It doesn't assert anything, either.
  */
  "A teacher with ActorLogging" must {
    "log a quote when a QuoteRequest message is sent" in {
      val teacherRef = TestActorRef[TeacherLogActor]
      teacherRef ! QuoteRequest
    }

    /*
    3. Asserts the internal state of the Log Actor.
    The method TestActorRef.underlyingActor calls the quoteList method
    defined in TeacherActor, which returns the list of quotes;
    it is used to assert its size.
    */
    "have a quote list of size 4" in {
      val teacherRef = TestActorRef[TeacherLogActor]
      teacherRef.underlyingActor.quoteList must have size 4
      teacherRef.underlyingActor.quoteList must have size 4
    }

    /*
    4. Verifying log messages from EventStream (after subscribing with ConfigFactory.parseString() in akka.testkit.TestKit)
    If there is no log message as a result of sending a message to the TeacherLogActor, the testcase will fail.
     */
    "be verifiable via EventFilter in response to a QuoteRequest that is sent" in {
      val teacherRef = TestActorRef[TeacherLogActor]
      // intercept for 1 log message which starts with QuoteResponse
      EventFilter.info(pattern = "QuoteResponse*", occurrences = 1) intercept {
        teacherRef ! QuoteRequest
      }
    }

    /*
      5. If the Actor accepts parameters
       */
    "have a quote list of the same size as the input parameter" in {

      val quotes = List(
        "Moderation is for cowards",
        "Anything worth doing is worth overdoing",
        "The trouble is you think you have time",
        "You never gonna know if you never even try")

      val teacherRef = TestActorRef(new TeacherLogParameterActor(quotes))
      //val teacherRef = TestActorRef(Props(new TeacherLogParameterActor(quotes)))

      teacherRef.underlyingActor.quoteList must have size 4
      EventFilter.info(pattern = "QuoteResponse*", occurrences = 1) intercept {
        teacherRef ! QuoteRequest
      }

    }

  }

  /*
  Finally, the afterAll lifecycle method in org.scalatest.BeforeAndAfterAll
  to shutdown the ActorSystem after the testcases are complete.
  The afterAll() method that the trait provides is more like the tearDown() in JUnit.
   */
  override def afterAll() {
    super.afterAll()
    //  system.shutdown() [@deprecated]
    system.terminate()
  }

}