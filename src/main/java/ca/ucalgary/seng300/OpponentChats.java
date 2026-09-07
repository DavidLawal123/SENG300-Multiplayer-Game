package ca.ucalgary.seng300;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class OpponentChats {

    // different bot personalities
    public enum OpponentPersonality {
        FRIENDLY,
        GAMER,
        TOXIC,
    }

    private static final int MAX_REPLIES = 3;
    private final Random rand = new Random();

    // setting the opponent personality
    private final OpponentPersonality personality;

    public OpponentChats() {
        int randomPersonality = rand.nextInt(OpponentPersonality.values().length);
        this.personality = OpponentPersonality.values()[randomPersonality];
    }

    // prevent opponent from reusing messages
    private final Set<String> usedFinishedMessages = new HashSet<>();
    private final Set<String> usedOngoingMessages = new HashSet<>();

    //set initial reply amount
    private int replyCounter = 0;

    private static final String[] FRIENDLY_ONGOING = {
            "Good luck, have fun!",
            "Whoa, nice one! I didn't see that coming.",
            "Whew, that was close. You almost had me there!",
            "Excited to see what you've got."
    };

    private static final String[] TOXIC_ONGOING = {
            "Free win for me, I guess. Try to keep up?",
            "My grandma plays faster than this. Is your monitor even on?",
            "Lucky guess. Don't get used to it."
    };

    private static final String[] GAMER_ONGOING = {
            "Gl hf!",
            "Calculated. Get outplayed!",
            "Focus up. I’m not dropping any rounds today."
    };

    public String getOngoingReply () {
        String[] pool = switch (this.personality) {
            case FRIENDLY -> pool = FRIENDLY_ONGOING;
            case TOXIC -> pool = TOXIC_ONGOING;
            case GAMER -> pool = GAMER_ONGOING;
        };

        String reply;

        do {
            reply = pool[rand.nextInt(pool.length)];
        } while (usedOngoingMessages.contains(reply) && usedOngoingMessages.size() < pool.length);
        usedOngoingMessages.add(reply);
        return reply;
    }

    // End of match responses
    private static final String[] FRIENDLY_WINNER = {
            "Good game!",
            "You were a tough opponent.",
            "Hope you have a great day!",
    };

    private static final String[] FRIENDLY_LOSER = {
            "Good game!",
            "You were a tough opponent.",
            "Hope you have a great day!",
    };

    private static final String[] GAMER_WINNER = {
            "gg",
            "ez clap",
            "ggwp"
    };

    private static final String[] GAMER_LOSER = {
            "gg",
            "i should've ffed",
            "are u a smurf"
    };

    private static final String[] TOXIC_WINNER = {
            "That was easy hahaha.",
            "I'm so good at this game.",
            "Get good."
    };

    private static final String[] TOXIC_LOSER = {
            "I'm reporting you!!!!!",
            "You must've been cheating!",
            "You're lucky I wasn't trying."
    };

    // obtaining the opponent replies
    public String getFinishedReply(boolean botWon) {
        if (replyCounter >= MAX_REPLIES) {
            return null;
        }
        replyCounter++;
        String[] pool = switch (personality) {
            case FRIENDLY -> botWon ? FRIENDLY_WINNER : FRIENDLY_LOSER;
            case GAMER -> botWon ? GAMER_WINNER : GAMER_LOSER;
            case TOXIC -> botWon ? TOXIC_WINNER : TOXIC_LOSER;
        };

        String reply;
        do {
            reply = pool[rand.nextInt(pool.length)];
        }
        while (usedFinishedMessages.contains(reply) && usedFinishedMessages.size() < pool.length);
        usedFinishedMessages.add(reply);
        return reply;
    }
}