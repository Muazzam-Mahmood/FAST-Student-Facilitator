package com.fast.fsf.carpool.state;

import com.fast.fsf.carpool.domain.Ride;

/**
 * Helper class for determining the moderation state of a ride.
 */
public final class RideModerationStates {

    private RideModerationStates() {}

    public static RideModerationState fromRide(Ride ride) {
        if (!ride.isApproved() && !ride.isFlagged()) {
            return PendingOpen.INSTANCE;
        }
        if (!ride.isApproved() && ride.isFlagged()) {
            return PendingFlagged.INSTANCE;
        }
        if (ride.isApproved() && !ride.isFlagged()) {
            return ApprovedOpen.INSTANCE;
        }
        return ApprovedFlagged.INSTANCE;
    }

    /** Pending submission state. */
    private enum PendingOpen implements RideModerationState {
        INSTANCE;

        @Override
        public void approve(RideModerationContext ctx, String moderationReasonOrNull) {
            Ride r = ctx.getRide();
            r.setApproved(true);
            r.setModerationReason(moderationReasonOrNull);
        }

        @Override
        public void flag(RideModerationContext ctx, String flagReasonOrNull) {
            Ride r = ctx.getRide();
            r.setFlagged(true);
            r.setModerationReason(flagReasonOrNull);
        }

        @Override
        public void resolve(RideModerationContext ctx) {
            /* Legacy controller allowed resolve even when nothing flagged — harmless no-op here. */
        }
    }

    /** Pending approval but flagged state. */
    private enum PendingFlagged implements RideModerationState {
        INSTANCE;

        @Override
        public void approve(RideModerationContext ctx, String moderationReasonOrNull) {
            Ride r = ctx.getRide();
            r.setApproved(true);
            r.setModerationReason(moderationReasonOrNull);
        }

        @Override
        public void flag(RideModerationContext ctx, String flagReasonOrNull) {
            ctx.getRide().setModerationReason(flagReasonOrNull);
        }

        @Override
        public void resolve(RideModerationContext ctx) {
            Ride r = ctx.getRide();
            r.setFlagged(false);
            r.setModerationReason(null);
        }
    }

    /** Approved and open state. */
    private enum ApprovedOpen implements RideModerationState {
        INSTANCE;

        @Override
        public void approve(RideModerationContext ctx, String moderationReasonOrNull) {
            ctx.getRide().setModerationReason(moderationReasonOrNull);
        }

        @Override
        public void flag(RideModerationContext ctx, String flagReasonOrNull) {
            Ride r = ctx.getRide();
            r.setFlagged(true);
            r.setModerationReason(flagReasonOrNull);
        }

        @Override
        public void resolve(RideModerationContext ctx) {
            /* Nothing to resolve — mirrors legacy behaviour (still saves identical entity snapshot). */
        }
    }

    /** Approved but flagged state. */
    private enum ApprovedFlagged implements RideModerationState {
        INSTANCE;

        @Override
        public void approve(RideModerationContext ctx, String moderationReasonOrNull) {
            Ride r = ctx.getRide();
            r.setApproved(true);
            r.setModerationReason(moderationReasonOrNull);
        }

        @Override
        public void flag(RideModerationContext ctx, String flagReasonOrNull) {
            ctx.getRide().setModerationReason(flagReasonOrNull);
        }

        @Override
        public void resolve(RideModerationContext ctx) {
            Ride r = ctx.getRide();
            r.setFlagged(false);
            r.setModerationReason(null);
        }
    }
}
