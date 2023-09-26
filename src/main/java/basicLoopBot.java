import devious_walker.DeviousWalker;
import net.runelite.api.coords.WorldPoint;
import net.runelite.rsb.methods.Methods;
import net.runelite.rsb.script.Script;
import net.runelite.rsb.script.ScriptManifest;
import net.runelite.rsb.wrappers.RSNPC;

import java.util.logging.Logger;

@ScriptManifest(
        authors = {"Baby Future"}, name = "Basic Loop Bot", version = 0.1,
        description = "<html><head>"
                + "</head><body>"
                + "<center>Basic example that kills chickens</center>"
                + "</body></html>"
)
public class basicLoopBot extends Script {

    private final WorldPoint chickenCoop = new WorldPoint(3203, 3293, 0);

    @Override
    public int loop() {
        int MIN = random(250, 400); // Random minimum value for our loop
        int MAX = random(700, 900); // Random maximum value for our loop
        // A Lazy way to catch exceptions - some NPE's can get thrown from the API
        // Let's try to find them if it happens

        try {
            // We should execute one action at a time,
            // when an action is complete we loop again
            if (getMyPlayer().isInCombat()) {
                Methods.sleep(700, 1200);
                Logger.getLogger(getClass().getName()).info("Already fighting");
                return 0;
                // We are already fighting
            }
            // Here's the start of our loop
            RSNPC chicken = this.ctx.npcs.getNearest("Cow"); // Find the nearest chicken
            // If there's not an NPC available, let's walk to the area
            if (chicken != null) {
                Logger.getLogger(getClass().getName()).info("Cow not null");
                if (getMyPlayer() == null) // Let's make sure our local player isn't Null
                    return 100;

                if (!chicken.isOnScreen() || (chicken.getLocation() != null && getMyPlayer().getLocation().distanceTo(chicken.getLocation()) > 10)) {
                    // We're a bit far, let's walk a little closer
                    DeviousWalker.walkTo(ctx, chicken.getLocation().getWorldLocation());
                    Logger.getLogger(getClass().getName()).info("Pathing to cow");
                    Methods.sleep(700, 1200);
                } else if (!chicken.isInCombat() && !chicken.isInteractingWithLocalPlayer() && !getMyPlayer().isInCombat()) {
                    // We passed our checks, let's attack a chicken now
                    if (chicken.doAction("Attack")) {
                        Logger.getLogger(getClass().getName()).info("Attacking cow");
                        // We successfully clicked attack
                        // TODO ideally the API should support a waitUntil type method
                        // i.e. you click attack and wait until your player is moving /
                        // isAttacking() returns true
                        int tries = 0;
                        while (!getMyPlayer().isInCombat()) {
                            Methods.sleep(700, 1200);
                            tries++;
                            if (tries > 5) {
                                break;
                            }
                        }
                        if (!getMyPlayer().isIdle() || getMyPlayer().isInCombat()) {
                            // Seems like our attack worked, we can exit
                            Logger.getLogger(getClass().getName()).info("Attacking worked");
                            return 0;
                        }

                        // An alternative to the sleep and if statement
                        /*do {
                            Methods.sleep(700, 1200);
                        } while (getMyPlayer().isLocalPlayerMoving());*/
                    }
                }
            } else {
                // Chicken is null, we should find one
                Logger.getLogger(getClass().getName()).info("Walking");
                DeviousWalker.walkTo(ctx, chickenCoop);
                Methods.sleep(700, 1200);
            }
        } catch (NullPointerException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // Randomize how often we loop
        // Ideally we can use PlayerProfile's that make this random per user/bot
        return random(MIN, MAX);
    }

    @Override
    public boolean onStart() {
        return true;
    }
}
