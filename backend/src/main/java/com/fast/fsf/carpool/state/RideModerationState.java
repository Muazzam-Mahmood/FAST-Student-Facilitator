package com.fast.fsf.carpool.state;

/**
 * Interface for ride moderation states.
 * Encapsulates the behavior for approving, flagging, and resolving rides.
 */
public interface RideModerationState {

    /** UC moderation: marks listing approved (still respects concurrent flagged combinations). */
    void approve(RideModerationContext ctx, String moderationReasonOrNull);

    /** Marks listing flagged for admin attention (reason may be {@code null}, mirroring legacy behaviour). */
    void flag(RideModerationContext ctx, String flagReasonOrNull);

    /** Clears moderation flag state exactly like the legacy controller (best-effort safe on every tuple). */
    void resolve(RideModerationContext ctx);
}
