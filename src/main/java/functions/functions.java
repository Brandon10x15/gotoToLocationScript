package functions;

import devious_walker.pathfinder.Walker;
import net.runelite.api.coords.WorldPoint;
import net.runelite.rsb.methods.NPCs;
import net.runelite.rsb.script.Script;
import net.runelite.rsb.wrappers.RSNPC;
import net.runelite.rsb.wrappers.RSPlayer;

import java.util.logging.Logger;

import static net.runelite.rsb.methods.Methods.random;
import static net.runelite.rsb.methods.Methods.sleep;

public class functions {
    public static class minMax {
        int min;
        int max;

        minMax(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    public static int getRandNum(int middle) {
        int randomMin = random(75, 125);
        int randomMax = random(75, 125);
        return random(middle - randomMin, middle - randomMax);
    }

    public static minMax getRandMinMax(int milliseconds) {
        int randomMin = random(225, 275);
        int randomMax = random(225, 275);
        return new minMax(getRandNum(milliseconds - randomMin), getRandNum(milliseconds + randomMax));
    }

    public static void sleepRand(int milliseconds) {
        minMax minMax = getRandMinMax(milliseconds);
        sleep(minMax.min, minMax.max);
    }

    public static RSNPC getNearestNPC(String[] npcs) {
        RSNPC npc = null;
        for (var name : npcs) {
            npc = NPCs.methods.npcs.getNearestNotInCombat(name); // Find the nearest npc
            if (npc != null) {
                break;
            }
        }
        return npc;
    }

    public static RSNPC getNearestNPC(int[] npcs) {
        RSNPC npc = null;
        for (var id : npcs) {
            npc = NPCs.methods.npcs.getNearestNotInCombat(id); // Find the nearest npc
            if (npc != null) {
                break;
            }
        }
        return npc;
    }

    public static void walkToWorldPoint(WorldPoint location, String name) {
        Walker.walkTo(location);
        Logger.getLogger("functions.walkToNPC").info("Walking towards " + name + ".");
        sleepRand(600);
    }

    public static void attackNPC(RSPlayer player, RSNPC npc) {

        try {
            if (npc.getAccessor() == null) {
                return;
            }
            if (npc.getAccessor().isInteracting()) {
                return;
            }
            int tries = 0;
            boolean attacked = false;
            while (!attacked) {
                try {
                    attacked = npc.doAction("Attack");
                } catch (Exception | Error e) {
                    e.printStackTrace();
                }
                tries++;
                if (tries > 5) {
                    tries = 0;
                    break;
                }
            }
            tries = 0;
            while (!player.isInCombat() && !player.isLocalPlayerMoving()) {
                sleepRand(600);
                tries++;
                if (tries > 4) {
                    tries = 0;
                    break;
                }
            }
        } catch (NullPointerException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        sleepRand(1000);
    }


    public static int attackNPCs(Script script, WorldPoint npcArea, String[] npcs) {
        try {
            RSPlayer player = script.getMyPlayer();
            if (player != null) { // Let's make sure our local player isn't Null
                // We should execute one action at a time,
                // when an action is complete we loop again
                if (player.isInCombat()) {
                    Logger.getLogger(script.getClass().getName()).info("Already fighting..");
                    sleepRand(600);
                    return getRandNum(600);
                    // We are already fighting
                }
                RSNPC npc = getNearestNPC(npcs);
                // If there's not an NPC available, let's walk to the area
                if (npc == null) {
                    walkToWorldPoint(npcArea, "NPC Area");
                    return getRandNum(600);
                }
                if (npc.getLocation() == null) {
                    walkToWorldPoint(npcArea, "NPC Area");
                    return getRandNum(600);
                }
                Logger.getLogger(script.getClass().getName()).info("Found " + npc.getName() + ".");

                if (!npc.isOnScreen() || (player.getLocation().distanceTo(npc.getLocation()) > 10)) {
                    // We're a bit far, let's walk a little closer
                    walkToWorldPoint(npc.getLocation().getWorldLocation(), npc.getName());
                    return getRandNum(600);
                }
                if (!npc.isInteractingWithLocalPlayer() && !player.isInCombat()) {


                    Logger.getLogger(script.getClass().getName()).info("Trying to attack " + npc.getName() + ".");
                    attackNPC(player, npc);
                    if (player.isInCombat()) {
                        // Seems like our attack worked, we can exit
                        Logger.getLogger(script.getClass().getName()).info("Attacking " + npc.getName() + ".");
                    }
                    return getRandNum(600);
                }
            }

        } catch (NullPointerException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return getRandNum(600);
    }
}
