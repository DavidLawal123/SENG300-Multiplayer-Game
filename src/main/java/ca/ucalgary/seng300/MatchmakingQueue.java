package ca.ucalgary.seng300;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class MatchmakingQueue {
    private final GameType gameType;
    private final ArrayList<MatchmakingTicket> waitingTickets;

    public MatchmakingQueue(GameType gameType) {
        this.gameType = Objects.requireNonNull(gameType, "gameType must not be null");
        this.waitingTickets = new ArrayList<>();
    }

    public GameType getGameType() {
        return this.gameType;
    }

    /** Enqueue a ticket. Ticket must match this queue's game type. */
    public void enqueue(MatchmakingTicket ticket) {
        Objects.requireNonNull(ticket, "ticket must not be null");

        if (ticket.getGameType() != this.gameType) {
            throw new IllegalArgumentException(
                "Ticket game type " + ticket.getGameType()
                + " does not match queue type " + this.gameType);
        }

        boolean alreadyExists = waitingTickets.stream()
            .anyMatch(t -> t.getUUID().equals(ticket.getUUID()));
        if (alreadyExists) {
            throw new IllegalArgumentException(
                "Ticket " + ticket.getUUID() + " already exists in queue.");
        }

        waitingTickets.add(ticket);
    }

    /**
     * Remove a ticket from the queue by UUID.
     * If found and still queued, this is treated as a cancellation.
     */
    public void remove(UUID ticketID) {
        Objects.requireNonNull(ticketID);

        for (int i = 0; i < waitingTickets.size(); i++) {
            MatchmakingTicket t = waitingTickets.get(i);
            if (t.getUUID().equals(ticketID)) {
                waitingTickets.remove(i);

                if (t.getStatus() == MatchmakingTicket.QueueStatus.QUEUED) {
                    t.cancel();
                }
                return;
            }
        }

        throw new IllegalArgumentException("No ticket found in queue for id: " + ticketID);
    }


    public List<MatchmakingTicket> getWaitingTickets() {
        return Collections.unmodifiableList(this.waitingTickets);
    }
}