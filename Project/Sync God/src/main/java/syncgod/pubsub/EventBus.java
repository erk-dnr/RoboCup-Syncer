package syncgod.pubsub;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import syncgod.pubsub.events.*;

/**
 * Pub-Sub Class for handling all Communications between Classes that aren't
 * directly linked to each other.
 *
 * @author tj18b
 */
public final class EventBus {

    /**
     * InstanceHolder Class for making the Access to Singleton Instance.
     * threadsafe
     */
    private static class InstanceHolder {
        private static final EventBus INSTANCE = new EventBus();
    }

    /**
     * Threadsafe hashmap with threadsafe ArrayList.
     */
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Object>> topics;

    private static final Logger LOG = Logger.getLogger(EventBus.class.getName());

    private EventBus() {
        topics = new ConcurrentHashMap<>();
        registerTopic(FrameChangeEvent.class.toString());
        registerTopic(LoadVideoEvent.class.toString());
        registerTopic(LoadLogEvent.class.toString());
        registerTopic(AddVideoEvent.class.toString());
        registerTopic(DeleteVideoEvent.class.toString());
        registerTopic(SetFrameEvent.class.toString());
        registerTopic(ProgressSkipEvent.class.toString());
        registerTopic(AddMarkerEvent.class.toString());
        registerTopic(RequestDataEvent.class.toString());
        registerTopic(AnswerTrackDataEvent.class.toString());
        registerTopic(AnswerVideoDataEvent.class.toString());
        registerTopic(CreateMarkerEvent.class.toString());
        registerTopic(ClearVideosEvent.class.toString());
        registerTopic(AnswerLogPathEvent.class.toString());
        registerTopic(LoadProjectEvent.class.toString());
        registerTopic(ProgressRequestEvent.class.toString());
        registerTopic(ProgressAnswerEvent.class.toString());
        registerTopic(DeleteMarkerEvent.class.toString());
    }

    /**
     * adding a new category to the pubsub.
     *
     * @param topic identifier of the new category
     */
    public void registerTopic(final String topic) {
        CopyOnWriteArrayList<Object> temp = new CopyOnWriteArrayList<>();
        topics.put(topic, temp);
    }

    /**
     * adding a new listener for a specific topic to the pubsub.
     *
     * @param topic category in which listener will be add
     * @param listener instance of class that is listening
     * @return whether the listener could be added
     */
    public boolean subscribe(final String topic, final Object listener) {
        if (topics.containsKey(topic)
                && !topics.get(topic).contains(listener)) {
            return topics.get(topic).add(listener);
        }
        return false;
    }

    /**
     * removing an existing listener for a specific topic to the pubsub.
     *
     * @param topic category from which listener will be removed
     * @param listener instance of class that will be removed
     * @return whether the listener could be removed
     */
    public boolean unsubscribe(final String topic, final Object listener) {
        if (topics.containsKey(topic) && topics.get(topic).contains(listener)) {
            return topics.get(topic).remove(listener);
        }
        return false;
    }

    /**
     * actual communication handler.
     *
     * @param ev event to use in message
     */
    public void post(final AbstractEvent ev) {
        if (topics.containsKey(ev.getClass().toString())) {
            topics.get(ev.getClass().toString()).forEach((Object item) -> {
                try {
                    java.lang.reflect.Method method;
                    method = item.getClass()
                            .getMethod("receive", ev.getClass());
                    method.invoke(item, ev);
                } catch (NoSuchMethodException
                        | SecurityException
                        | IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException ex) {
                    LOG.log(Level.WARNING, "Error in Post-Method Call", ex);
                }
            });
        }
    }

    /**
     * getter method for singleton access.
     *
     * @return instance of pubsub
     */
    public static EventBus getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
