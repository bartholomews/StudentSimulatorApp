package test

import akka.actor.ActorSystem
import akka.testkit._
import com.typesafe.config.ConfigFactory
import main.deathwatch.{QuoteActorWatcher, QuoteRepositoryActor}
import main.protocols.QuoteRepositoryProtocol.{QuoteRepositoryResponse, QuoteRepositoryRequest}
import main.protocols.TeacherProtocol.QuoteRequest
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import scala.concurrent.duration._

/**
  * @see http://rerun.me/2014/10/31/akka-notes-deathwatch-7/
  */
class QuoteRepositoryActorTest extends TestKit(ActorSystem("TestQuoteActorWatcher",
  ConfigFactory.parseString("""
                               akka {
                                loggers = ["akka.testkit.TestEventListener"]
                                test {
                                  filter-leeway = 7s
                                }
                               } """)))

  with WordSpecLike with MustMatchers with BeforeAndAfterAll with ImplicitSender {

  "A QuoteRepositoryActor" must {

    val testProbe = TestProbe()
    val quoteRepository = TestActorRef[QuoteRepositoryActor]

    within(1000 millis) {
      var receivedQuotes = List[String]()
      (1 to 3).foreach(_ => quoteRepository ! QuoteRepositoryRequest)
      receiveWhile() {
        case QuoteRepositoryResponse(quote) => receivedQuotes = receivedQuotes :+ quote
      }
      "send back a quote response for 3 times without any error" in {

        println(s"receiveCount ${receivedQuotes.size}")
        receivedQuotes.size must be(3)
      }
      /*
        The QuoteRepositoryActor should send the testcase a Terminated message
        on receipt of the 4th message.
       */
      "send back a termination message to the watcher on 4th message" in {
        testProbe.watch(quoteRepository)
        quoteRepository ! QuoteRepositoryRequest
        testProbe.expectTerminated(quoteRepository)
      }
      "not send back a termination message on 4th message if not watched" in {
        testProbe.unwatch(quoteRepository)
        quoteRepository ! QuoteRepositoryRequest
        testProbe.expectNoMsg()
      }
    }

    "end back a termination message to the watcher on 4th message to the TeacherActor" in {

      val watcher = TestActorRef[QuoteActorWatcher]

      /*
        Send 3 messages to the QuoteRepositoryActor
       */
      within(1000 millis) {
        (1 to 3).foreach(_ => watcher ! QuoteRequest)

        /*
          Send the 4th message:
          the pattern="""Child Actor .* Terminated"""
          is expected to match a log message which is
          of the format:
            Child Actor
            {Actor[akka://TestQuoteActorWatcher/user/$$b/quoteRepActor#2136866194]}
            Terminated
         */
        EventFilter.error(pattern ="""Child Actor .* Terminated""", occurrences = 1).intercept {
          watcher ! QuoteRequest
        }
      }
    }
  }

  override def afterAll() {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
    Thread.sleep(2000)
  }

}
