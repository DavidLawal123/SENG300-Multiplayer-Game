package ca.ucalgary.seng300;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.UUID;

public class EventBusAndHandlerEventSystemTest {

    /**
     * Test registering a single handler
     * Verifies that the handler is added correctly
     */
    @Test
    public void testRegisterSingleHandler() {
        EventBus eventBus = new EventBus();

        //adds one handler
        UUID uuid = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);

        //expect size = 1
        assertEquals(1, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());
    }

    /**
     * Test registering multiple handlers for the same event
     * Verifies that all handlers are stored
     */
    @Test
    public void testRegisterMultipleHandler() {
        EventBus eventBus = new EventBus();

        //add two handlers
        eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);

        //expect size = 2
        assertEquals(2, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());

    }

    /**
     * Test Registering handlers for different events
     * Verifies that different keys are created in the map
     */
    @Test
    public void testRegisterDifferentEvents() {
        EventBus eventBus = new EventBus();

        eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> {
        }, Event.USER_DID_EXIT_LOBBY);

        assertTrue(eventBus.handlers.containsKey(Event.USER_DID_JOIN_LOBBY));
        assertTrue(eventBus.handlers.containsKey(Event.USER_DID_EXIT_LOBBY));
    }

    /**
     * Tests that each handler gets a unique UUID
     */
    @Test
    public void testRegisterUniqueUUID() {
        EventBus eventBus = new EventBus();

        UUID uuid1 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        UUID uuid2 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_CREATE_LOBBY);

        //the UUIDs should not be the same
        assertNotEquals(uuid1, uuid2);
    }

    /**
     * Test registering a null Runnable
     * Checks the system behavior when closure is null
     * Handler enforces non-null closures using requireNonNull, so NullPointerException is thrown to handle the situation
     */
    @Test(expected = NullPointerException.class)
    public void testRegisterNullRunnable() {
        EventBus eventBus = new EventBus();
        eventBus.registerHandler(null, Event.USER_DID_JOIN_LOBBY);
    }

    /**
     * Test removing an existing handler
     * Verifies that the handler is removed correctly
     */
    @Test
    public void testRemoveExistingHandler() {
        EventBus eventBus = new EventBus();

        //adds handler
        UUID uuid = eventBus.registerHandler(() -> {
        }, Event.USER_DID_QUEUE);

        //removes handler
        eventBus.removeHandler(uuid);

        //the list should now be empty
        assertEquals(0, eventBus.handlers.get(Event.USER_DID_QUEUE).size());
    }

    /**
     * Test removing one handler from multiple handlers
     */
    @Test
    public void testRemoveOneFromMultiple() {
        EventBus eventBus = new EventBus();

        //add two handlers
        UUID uuid = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);

        //remove one
        eventBus.removeHandler(uuid);

        //one should remain
        assertEquals(1, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());
    }

    @Test
    public void testRemoveOneFromMultiple1() {
        EventBus eventBus = new EventBus();

        //add three handlers
        UUID uuid = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);

        //remove one
        eventBus.removeHandler(uuid);

        //two should remain
        assertEquals(2, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());
    }

    @Test
    public void testRemoveOneFromMultiple2() {
        EventBus eventBus = new EventBus();

        //add three handlers
        UUID uuid1 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        UUID uuid2 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        UUID uuid3 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);

        //remove two
        eventBus.removeHandler(uuid1);
        eventBus.removeHandler(uuid2);

        //one should remain
        assertEquals(1, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());
    }

    /**
     * Test removing handler affects only correct event
     */
    @Test
    public void testRemoveFromCurrentEvent() {
        EventBus eventBus = new EventBus();

        //add handlers to two events
        UUID uuid = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> {
        }, Event.LOBBY_DESTROYED);

        //remove only one
        eventBus.removeHandler(uuid);

        //first event should be empty
        assertEquals(0, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());

        //second event should still have 1 handler
        assertEquals(1, eventBus.handlers.get(Event.LOBBY_DESTROYED).size());
    }

    @Test
    public void testRemoveFromCurrentEvent1() {
        EventBus eventBus = new EventBus();

        //add handlers to three events
        UUID uuid1 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        UUID uuid2 = eventBus.registerHandler(() -> {
        }, Event.LOBBY_DESTROYED);
        UUID uuid3 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_CANCEL_QUEUE);

        //remove only one
        eventBus.removeHandler(uuid1);

        //first event should be empty
        assertEquals(0, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());

        //second event should still have 1 handler
        assertEquals(1, eventBus.handlers.get(Event.LOBBY_DESTROYED).size());

        //third event also should still have 1 handler
        assertEquals(1, eventBus.handlers.get(Event.USER_DID_CANCEL_QUEUE).size());
    }

    @Test
    public void testRemoveFromCurrentEvent2() {
        EventBus eventBus = new EventBus();

        //add handlers to three events
        UUID uuid1 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_JOIN_LOBBY);
        UUID uuid2 = eventBus.registerHandler(() -> {
        }, Event.LOBBY_DESTROYED);
        UUID uuid3 = eventBus.registerHandler(() -> {
        }, Event.USER_DID_CANCEL_QUEUE);

        //remove two
        eventBus.removeHandler(uuid1);
        eventBus.removeHandler(uuid3);

        //first event should be empty
        assertEquals(0, eventBus.handlers.get(Event.USER_DID_JOIN_LOBBY).size());

        //second event should still have 1 handler
        assertEquals(1, eventBus.handlers.get(Event.LOBBY_DESTROYED).size());

        //third event also should be empty
        assertEquals(0, eventBus.handlers.get(Event.USER_DID_CANCEL_QUEUE).size());
    }

    /**
     * Test removing invalid uuid
     * should throw IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveInvalidUUID() {
        EventBus eventBus = new EventBus();

        //tries removing a random uuid
        eventBus.removeHandler(UUID.randomUUID());
    }

    /**
     * Test removing from empty EventBus
     * should throw exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveFromEmptyEventBus() {
        EventBus eventBus = new EventBus();

        //no handlers exists
        eventBus.removeHandler(UUID.randomUUID());
    }

    /**
     * Test publishing event with one handler
     * Verifies that handler is executed
     */
    @Test
    public void testPublishSingleHandler() {
        EventBus eventBus = new EventBus();

        final boolean[] called = {false};

        //register handler that changes the value
        eventBus.registerHandler(() -> called[0] = true, Event.USER_DID_JOIN_LOBBY);

        //publish event
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //checks if handler ran
        assertTrue(called[0]);
    }

    /**
     * Test publishing event with multiple handlers
     * all handlers should run
     */
    @Test
    public void testPublishMultipleHandlers() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //add two handlers
        eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //publish event
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //both should run
        assertEquals(2, count[0]);
    }

    @Test
    public void testPublishMultipleHandlers1() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //add three handlers
        eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //publish event
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //all three should run
        assertEquals(3, count[0]);
    }

    /**
     * Test publishing event with no handlers
     */
    @Test
    public void testPublishNoHandlers() {
        EventBus eventBus = new EventBus();

        //no handlers added
        eventBus.publishEvent(Event.USER_DID_CANCEL_QUEUE);

        //if there is no crash test should pass
        assertTrue(true);
    }

    /**
     * Test publishing after handler removal, handler should not be executed
     */
    @Test
    public void testPublishAfterRemove() {
        EventBus eventBus = new EventBus();

        final boolean[] called = {false};

        //add handler
        UUID uuid = eventBus.registerHandler(() -> called[0] = true, Event.USER_DID_JOIN_LOBBY);

        //remove handler
        eventBus.removeHandler(uuid);

        //publish event
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //should not run
        assertFalse(called[0]);
    }

    /**
     * Test publishing only triggers correct event handlers
     */
    @Test
    public void testPublishCorrectEventOnly() {
        EventBus eventBus = new EventBus();

        final boolean[] called = {false};

        //register handler for different event
        eventBus.registerHandler(() -> called[0] = true, Event.USER_DID_JOIN_LOBBY);

        //publish some other event
        eventBus.publishEvent(Event.LOBBY_DESTROYED);

        //should not run
        assertFalse(called[0]);
    }

    @Test
    public void testPublishCorrectEventOnly1() {
        EventBus eventBus = new EventBus();

        final boolean[] called = {false};

        //register handler
        eventBus.registerHandler(() -> called[0] = true, Event.USER_DID_JOIN_LOBBY);

        //publish same event
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //should run
        assertTrue(called[0]);
    }

    @Test
    public void testEntireFunctionality() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //register handler
        UUID uuid = eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //first publish
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //remove handler
        eventBus.removeHandler(uuid);

        //publish again
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //should run only once
        assertEquals(1, count[0]);
    }

    /**
     * test multiple publishes before removing handler
     * handler should run multiple times before removal
     */
    @Test
    public void testEntireFunctionality1() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //register handler
        UUID uuid = eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //publish 3 times
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //remove handler
        eventBus.removeHandler(uuid);

        //publish again
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //should run only 3 times
        assertEquals(3, count[0]);
    }

    /**
     * test multiple handlers where one is removed
     * only remaining handler should run
     */
    @Test
    public void testEntireFunctionality2() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //register two handlers
        UUID uuid1 = eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);
        eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //remove handler
        eventBus.removeHandler(uuid1);

        //publish again
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //only one handler should run
        assertEquals(1, count[0]);
    }

    /**
     * test removing handler before any publish
     * handler should never run
     */
    @Test
    public void testEntireFunctionality3() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //register handler
        UUID uuid = eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //remove immediately
        eventBus.removeHandler(uuid);

        //publish event
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //should never run
        assertEquals(0, count[0]);
    }

    /**
     * test publishing a different event
     */
    @Test
    public void testEntireFunctionality4() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //register handler
        UUID uuid = eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //publish  a different event
        eventBus.publishEvent(Event.USER_DID_CREATE_LOBBY);

        //should not run
        assertEquals(0, count[0]);
    }

    /**
     * test registering again after removal
     * handler should work again
     */
    @Test
    public void testEntireFunctionality5() {
        EventBus eventBus = new EventBus();

        final int[] count = {0};

        //register handler
        UUID uuid = eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //remove immediately
        eventBus.removeHandler(uuid);

        //register again
        eventBus.registerHandler(() -> count[0]++, Event.USER_DID_JOIN_LOBBY);

        //publish event
        eventBus.publishEvent(Event.USER_DID_JOIN_LOBBY);

        //should run once
        assertEquals(1, count[0]);
    }

    /**
     * Test creating handler with valid runnable
     */
    @Test
    public void testConstructorValid() {
        Handler handler = new Handler(() -> {
        });

        assertNotNull(handler);
    }

    /**
     * Test creating handler with null closure, should throe exception
     */
    @Test(expected = NullPointerException.class)
    public void testConstructorNull() {
        new Handler(null);
    }

    /**
     * Test UUID is generated
     */
    @Test
    public void testUUIDNotNUll() {
        Handler handler = new Handler(() -> {
        });

        assertNotNull(handler.getUUID());
    }

    /**
     * Test each handler has unique UUID
     */
    @Test
    public void testUUIDUnique() {
        Handler handler1 = new Handler(() -> {
        });
        Handler handler2 = new Handler(() -> {
        });

        assertNotEquals(handler1.getUUID(), handler2.getUUID());
    }

    /**
     * Test getClosure returns same Runnable
     */
    @Test
    public void testGetClosure() {
        Runnable runnable = () -> {
        };
        Handler handler = new Handler(runnable);

        assertEquals(runnable, handler.getClosure());
    }

    /**
     * Test closure executes correctly
     */
    @Test
    public void testClosureExecution() {
        final boolean[] called = {false};
        Handler handler = new Handler(() -> called[0] = true);

        handler.getClosure().run();
        assertTrue(called[0]);
    }

    /**
     * Test UUID stays the same for the same handler
     */
    @Test
    public void testUUIDConsistency() {
        Handler handler = new Handler(() -> {
        });

        UUID uuid1 = handler.getUUID();
        UUID uuid2 = handler.getUUID();

        assertEquals(uuid1, uuid2);
    }

    /**
     * Test UUID different for different handlers
     */
    @Test
    public void testUUIDConsistency1() {
        Handler handler1 = new Handler(() -> {
        });
        Handler handler2 = new Handler(() -> {
        });

        UUID uuid1 = handler1.getUUID();
        UUID uuid2 = handler2.getUUID();

        assertNotEquals(uuid1, uuid2);
    }

    /**
     * Test multiple handlers behave independently
     */
    @Test
    public void testMultipleHandlers() {
        final int[] count = {0};

        Handler handler1 = new Handler(() -> count[0]++);
        Handler handler2 = new Handler(() -> count[0] += 2);

        handler1.getClosure().run();
        handler2.getClosure().run();

        assertEquals(3, count[0]);
    }

    /**
     * same test with different possibilities
     */
    @Test
    public void testMultipleHandlers1() {
        final int[] count = {0};

        Handler handler1 = new Handler(() -> count[0] += 2);
        Handler handler2 = new Handler(() -> count[0] += 3);

        handler1.getClosure().run();
        handler2.getClosure().run();

        assertEquals(5, count[0]);
    }

    /**
     * same test with different possibilities
     */
    @Test
    public void testMultipleHandlers2() {
        final int[] count = {0};

        Handler handler1 = new Handler(() -> count[0] += 2);
        Handler handler2 = new Handler(() -> count[0] += 3);
        Handler handler3 = new Handler(() -> count[0] += 4);

        handler1.getClosure().run();
        handler2.getClosure().run();
        handler3.getClosure().run();

        assertEquals(9, count[0]);
    }
}