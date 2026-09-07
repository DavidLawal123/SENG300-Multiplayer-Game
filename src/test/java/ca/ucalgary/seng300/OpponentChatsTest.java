package ca.ucalgary.seng300;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class OpponentChatsTest {
    //all possible ongoing replies from all personalities
    private static final Set<String> ALL_ONGOING_REPLIES = Set.of(
            "Good luck, have fun!",
            "Whoa, nice one! I didn't see that coming.",
            "Whew, that was close. You almost had me there!",
            "Excited to see what you've got.",
            "Free win for me, I guess. Try to keep up?",
            "My grandma plays faster than this. Is your monitor even on?",
            "Lucky guess. Don't get used to it.",
            "Gl hf!",
            "Calculated. Get outplayed!",
            "Focus up. I’m not dropping any rounds today."
    );

    //all possible finished replies when bot won
    private static final Set<String> ALL_FINISHED_WIN_REPLIES = Set.of(
            "Good game!",
            "You were a tough opponent.",
            "Hope you have a great day!",
            "gg",
            "ez clap",
            "ggwp",
            "That was easy hahaha.",
            "I'm so good at this game.",
            "Get good."
    );

    //all possible finished replies when bot lost
    private static final Set<String> ALL_FINISHED_LOSS_REPLIES = Set.of(
            "Good game!",
            "You were a tough opponent.",
            "Hope you have a great day!",
            "gg",
            "i should've ffed",
            "are u a smurf",
            "I'm reporting you!!!!!",
            "You must've been cheating!",
            "You're lucky I wasn't trying."
    );

    /**
     * Test that OpponentChats object is created successfully
     */
    @Test
    public void testConstructorCreatesObject(){
        //create object
        OpponentChats chats = new OpponentChats();

        //get reply
        String reply = chats.getOngoingReply();

        //reply should not be null
        assertNotNull(reply);
    }

    /**
     * Test that getOngoingReply returns a valid message from one of the reply categories
     */
    @Test
    public void testGetOngoingReplyKnown(){
        //create object
        OpponentChats chats = new OpponentChats();

        //get reply
        String reply = chats.getOngoingReply();

        //reply should be from known ongoing messages
        assertTrue(ALL_ONGOING_REPLIES.contains(reply));
    }

    /**
     * Test that the first three ongoing replies are unique
     */
    @Test
    public void testGetOngoingReplyFirstThreeUnique(){
        //create object
        OpponentChats chats = new OpponentChats();

        //store first three replies
        Set<String> replies = new HashSet<>();
        replies.add(chats.getOngoingReply());
        replies.add(chats.getOngoingReply());
        replies.add(chats.getOngoingReply());

        //first three should be unique for every personality
        assertEquals(3, replies.size());
    }

    /**
     * Test that ongoing replies are still valid after multiple calls
     */
    @Test
    public void testGetOngoingReplyMultipleCallsValid(){
        //create object
        OpponentChats chats = new OpponentChats();

        //call multiple times
        for (int i = 0; i < 10; i++){
            String reply = chats.getOngoingReply();

            //every reply should be from a known category
            assertNotNull(reply);
            assertTrue(ALL_ONGOING_REPLIES.contains(reply));
        }
    }

    /**
     * Test that reply for a win returns a non-null message
     */
    @Test
    public void testGetFinishedReplyWinNotNull(){
        //create object
        OpponentChats chats = new OpponentChats();

        //get winner reply
        String reply = chats.getFinishedReply(true);

        //reply should not be not null
        assertNotNull(reply);
    }

    /**
     * Test that reply for a loss returns a non-null message
     */
    @Test
    public void testGetFinishedReplyLossNotNull(){
        //create object
        OpponentChats chats = new OpponentChats();

        //get loser reply
        String reply = chats.getFinishedReply(false);

        //reply should not be not null
        assertNotNull(reply);
    }

    /**
     * Test that first three finished win replies are unique
     */
    @Test
    public void testGetFinishedReplyWinFirstThreeUnique(){
        //create object
        OpponentChats chats = new OpponentChats();

        //first three replies
        Set<String> replies = new HashSet<>();
        replies.add(chats.getFinishedReply(true));
        replies.add(chats.getFinishedReply(true));
        replies.add(chats.getFinishedReply(true));

        //first three should be unique
        assertEquals(3, replies.size());
    }

    /**
     * Test that first three finished loss replies are unique
     */
    @Test
    public void testGetFinishedReplyLossFirstThreeUnique(){
        //create object
        OpponentChats chats = new OpponentChats();

        //first three replies
        Set<String> replies = new HashSet<>();
        replies.add(chats.getFinishedReply(false));
        replies.add(chats.getFinishedReply(false));
        replies.add(chats.getFinishedReply(false));

        //first three should be unique
        assertEquals(3, replies.size());
    }

    /**
     * Test that finished replies stop after three calls
     */
    @Test
    public void testGetFinishedReplyStopsAfterThree(){
        //create object
        OpponentChats chats = new OpponentChats();

        //first three replies should exist
        assertNotNull(chats.getFinishedReply(true));
        assertNotNull(chats.getFinishedReply(true));
        assertNotNull(chats.getFinishedReply(true));

        //fourth reply should be null
        assertNull(chats.getFinishedReply(true));
    }

    /**
     * Test that finished replies stops after three calls even if win/loss changes.
     */
    @Test
    public void testGetFinishedReplyStopsAfterThreeMixedCalls() {
        //create object
        OpponentChats chats = new OpponentChats();

        //mixed calls still count toward same reply counter
        assertNotNull(chats.getFinishedReply(true));
        assertNotNull(chats.getFinishedReply(false));
        assertNotNull(chats.getFinishedReply(true));

        //fourth total call should return null
        assertNull(chats.getFinishedReply(false));
    }

    /**
     * Test that separate objects have separate finished reply counters.
     */
    @Test
    public void testDifferentObjectsHaveIndependentFinishedCounters() {
        //create two different objects
        OpponentChats chats1 = new OpponentChats();
        OpponentChats chats2 = new OpponentChats();

        //use up first object
        assertNotNull(chats1.getFinishedReply(true));
        assertNotNull(chats1.getFinishedReply(true));
        assertNotNull(chats1.getFinishedReply(true));
        assertNull(chats1.getFinishedReply(true));

        // second object should still work independently
        assertNotNull(chats2.getFinishedReply(true));
    }
    /**
     * Test that separate objects have independent ongoing message history.
     */
    @Test
    public void testDifferentObjectsHaveIndependentOngoingHistory() {
        //create two different objects
        OpponentChats chats1 = new OpponentChats();
        OpponentChats chats2 = new OpponentChats();

        //each object should still return valid reply
        String reply1 = chats1.getOngoingReply();
        String reply2 = chats2.getOngoingReply();

        assertNotNull(reply1);
        assertNotNull(reply2);
        assertTrue(ALL_ONGOING_REPLIES.contains(reply1));
        assertTrue(ALL_ONGOING_REPLIES.contains(reply2));
    }

    /**
     * Test that repeated finished win calls before limit always return valid known messages.
     */
    @Test
    public void testFinishedWinRepliesBeforeLimitAreAlwaysValid() {
        //create object
        OpponentChats chats = new OpponentChats();

        //first three calls should be valid
        for (int i = 0; i < 3; i++) {
            String reply = chats.getFinishedReply(true);

            assertNotNull(reply);
            assertTrue(ALL_FINISHED_WIN_REPLIES.contains(reply));
        }
    }

    /**
     * Test that repeated finished loss calls before limit always return valid known messages.
     */
    @Test
    public void testFinishedLossRepliesBeforeLimitAreAlwaysValid() {
        //create object
        OpponentChats chats = new OpponentChats();

        // first three calls should be valid
        for (int i = 0; i < 3; i++) {
            String reply = chats.getFinishedReply(false);

            assertNotNull(reply);
            assertTrue(ALL_FINISHED_LOSS_REPLIES.contains(reply));
        }
    }
}
