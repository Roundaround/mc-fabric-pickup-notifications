package me.roundaround.pickupnotifications.gametest;

import me.roundaround.allay.api.gametest.ServerGameTest;
import me.roundaround.trove.gametest.ServerSmokeTest;

/**
 * Asserts Pickup Notifications boots a dedicated server cleanly and loads no client code.
 * The body lives in {@link ServerSmokeTest}; this opt-in subclass just carries the
 * {@code @ServerGameTest} marker the build-time scan discovers. As a client-only HUD mod,
 * the "no client class loaded" assertion proves it's a genuine server no-op.
 */
@ServerGameTest
public class PickupNotificationsServerSmokeTest extends ServerSmokeTest {
}
