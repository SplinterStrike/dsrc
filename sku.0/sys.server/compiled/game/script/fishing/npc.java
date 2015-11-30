package script.fishing;

import script.*;
import script.base_class.*;
import script.combat_engine.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import script.base_script;

import script.library.sui;
import script.library.utils;
import script.library.minigame;

public class npc extends script.base_script
{
    public npc()
    {
    }
    public int OnDetach(obj_id self) throws InterruptedException
    {
        minigame.cleanupFishing(self);
        return SCRIPT_CONTINUE;
    }
    public int OnEnteredCombat(obj_id self) throws InterruptedException
    {
        minigame.stopFishing(self);
        return SCRIPT_CONTINUE;
    }
    public int handleFishingTick(obj_id self, dictionary params) throws InterruptedException
    {
        return SCRIPT_CONTINUE;
    }
    public int handlePlayCastSplash(obj_id self, dictionary params) throws InterruptedException
    {
        minigame.playCastSplash(self, params);
        messageTo(self, minigame.HANDLER_FISHING_TICK, null, minigame.FISHING_TICK, false);
        return SCRIPT_CONTINUE;
    }
}
