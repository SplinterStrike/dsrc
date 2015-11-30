package script.theme_park.heroic.ig88;

import script.*;
import script.base_class.*;
import script.combat_engine.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import script.base_script;

import script.library.trial;
import script.library.utils;

public class ig88_controller extends script.base_script
{
    public ig88_controller()
    {
    }
    public int ig88Died(obj_id self, dictionary params) throws InterruptedException
    {
        if (!isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }
        trial.setDungeonCleanOutTimer(self, 15);
        return SCRIPT_CONTINUE;
    }
    public int killNPC(obj_id self, dictionary params) throws InterruptedException
    {
        if (!isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = params.getObjId("npc");
        playClientEffectLoc(npc, "clienteffect/ig88_bomb_droid_explode.cef", getLocation(npc), 0);
        trial.cleanupNpc(npc);
        return SCRIPT_CONTINUE;
    }
    public int ig88_failed(obj_id self, dictionary params) throws InterruptedException
    {
        if (!isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }
        if (utils.hasScriptVar(self, "ig88.failure_time"))
        {
            int lastFailure = utils.getIntScriptVar(self, "ig88.failure_time");
            int messageTime = params.getInt("failure_time");
            if (messageTime != lastFailure)
            {
                return SCRIPT_CONTINUE;
            }
        }
        obj_id[] spawn_ids = trial.getObjectsInDungeonWithObjVar(self, "spawn_id");
        dictionary sessionDict = new dictionary();
        if (spawn_ids != null && spawn_ids.length > 0)
        {
            for (int i = 0; i < spawn_ids.length; i++)
            {
                if (isIdValid(spawn_ids[i]))
                {
                    messageTo(spawn_ids[i], "shoutFailed", sessionDict, 1, false);
                }
            }
        }
        messageTo(self, "restartSpawn", sessionDict, 5, false);
        return SCRIPT_CONTINUE;
    }
    public int ig88_failure_check(obj_id self, dictionary params) throws InterruptedException
    {
        if (!isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id[] targets = trial.getValidTargetsInCell(self, "r1");
        dictionary sessionDict = new dictionary();
        if (targets == null || targets.length <= 0)
        {
            int gameTime = getGameTime();
            utils.setScriptVar(self, "ig88.failure_time", gameTime);
            sessionDict.put("failure_time", gameTime);
            int lastFailureTime = utils.getIntScriptVar(self, "ig88.last_failure_time");
            if (gameTime - lastFailureTime > 1)
            {
                utils.setScriptVar(self, "ig88.last_failure_time", gameTime);
                messageTo(self, "ig88_failed", sessionDict, 1, false);
            }
            return SCRIPT_CONTINUE;
        }
        else 
        {
            messageTo(self, "ig88_failure_check", sessionDict, 5, false);
        }
        return SCRIPT_CONTINUE;
    }
}
