
package syncgod.pubsub;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import syncgod.pubsub.events.AbstractEvent;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Jonas
 */
public class EventBusTest {

    @BeforeClass
    public static void setUpClass() {
    System.out.println("Testing EventBus");
        }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Done Testing EventBus");
    }

    /**
     * Should throw NullPointerException when trying to register topic thats null
     */
    @Test(expected = NullPointerException.class)
    public void testRegisterTopic() {
        System.out.println("Testing registerTopic");
        String topic = null;
        EventBus instance = EventBus.getInstance();

        instance.registerTopic(topic);
    }

    /**
     * Subscribing a listener for the first time should return true,
     * an attempt to subscribe a second time however should return false.
     */
    @Test
    public void testSubscribe() {
        System.out.println("Testing subscribe");
        String topic = "testTopic";
        Object listener = new Object();
        EventBus instance = EventBus.getInstance();
        instance.registerTopic(topic);

        Boolean firstSubAttempt = instance.subscribe(topic, listener);
        assertThat(firstSubAttempt, is(true));

        Boolean secondSubAttempt = instance.subscribe(topic, listener);
        assertThat(secondSubAttempt, is(false));
    }

    /**
     * Unsubscribing a listener for the first time should return true,
     * an attempt to unsubscribe a second time however should return false.
     */
    @Test
    public void testUnsubscribe() {
        System.out.println("Testing unsubscribe");
        String topic = "testTopic";
        Object listener = new Object();
        EventBus instance = EventBus.getInstance();
        instance.registerTopic(topic);
        instance.subscribe(topic, listener);

        Boolean firstUnsubAttempt = instance.unsubscribe(topic, listener);
        assertThat(firstUnsubAttempt, is(true));

        Boolean secondUnsubAttempt = instance.unsubscribe(topic, listener);
        assertThat(secondUnsubAttempt, is(false));
    }

    /**
     * The receive method of the MockReceiver should be invoked when posting to it,
     * setting its 'received' flag to true.
     */
    @Test
    public void testPost() {
        System.out.println("Testing post");
        Boolean received;
        MockReceiver mockReceiver = new MockReceiver();
        AbstractEvent event = new MockEvent(new Object());
        EventBus instance = EventBus.getInstance();
        instance.registerTopic(event.getClass().toString());
        instance.subscribe(event.getClass().toString(), mockReceiver);

        instance.post(event);
        received = mockReceiver.received;
        assertThat(received, is(true));
    }






    //Mock classes to test posting messages to subscribers



    /**
     * Mock receiver to post test message to.
     */
    private class MockReceiver{
        Boolean received = false;

        MockReceiver(){
        }

        public void receive(MockEvent ev){
            received = true;
        }
    }

    /**
     * A mock AbstractEvent.
     */
    private class MockEvent extends AbstractEvent {

        Object message;

        MockEvent(Object message){
            this.message = message;
        }

        public Object getMessage() {
            return null;
        }
    }
}
