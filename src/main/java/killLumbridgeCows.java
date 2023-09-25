import net.runelite.api.coords.WorldPoint;
import net.runelite.rsb.script.Script;
import net.runelite.rsb.script.ScriptManifest;

@ScriptManifest(
        authors = {"Brandon10x15"}, name = "Kill Cows", version = 0.1,
        description = "<html><head>"
                + "</head><body>"
                + "<center>Navigate to and kill Lumbridge cows.</center>"
                + "</body></html>"
)
public class killLumbridgeCows extends Script {

    private final WorldPoint npcArea = new WorldPoint(3203, 3293, 0);
    private final String[] npcNames = new String[]{"Cow", "Cow calf"};

    @Override
    public int loop() {
        try {
            return functions.functions.attackNPCs(this, npcArea, npcNames);
        } catch (Exception | Error e) {
            return functions.functions.getRandNum(500);
        }
    }

    @Override
    public boolean onStart() {
        return true;
    }
}
