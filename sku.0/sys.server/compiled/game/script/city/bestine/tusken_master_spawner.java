package script.city.bestine;

import script.*;
import script.base_class.*;
import script.combat_engine.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import script.base_script;

import script.library.create;
import script.library.utils;

public class tusken_master_spawner extends script.base_script
{
    public tusken_master_spawner()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        deltadictionary tuskenSpawn = self.getScriptVars();
        tuskenSpawn.put("count", 0);
        messageTo(self, "checkSpawnStatus", null, 60, false);
        return SCRIPT_CONTINUE;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        deltadictionary tuskenSpawn = self.getScriptVars();
        tuskenSpawn.put("count", 0);
        messageTo(self, "checkSpawnStatus", null, 60, false);
        return SCRIPT_CONTINUE;
    }
    public int checkSpawnStatus(obj_id self, dictionary params) throws InterruptedException
    {
        int count = utils.getIntScriptVar(self, "count");
        if (count == 0)
        {
            int delay = 3600 + rand(300, 900);
            messageTo(self, "spawnTuskenNpcs", null, delay, false);
        }
        return SCRIPT_CONTINUE;
    }
    public int spawnTuskenNpcs(obj_id self, dictionary params) throws InterruptedException
    {
        deltadictionary tuskenSpawn = self.getScriptVars();
        int count = tuskenSpawn.getInt("count");
        obj_id building = getTopMostContainer(self);
        location tusken01Location = new location(6.54f, 8.01f, -34.17f, "tatooine", getCellId(building, "r5"));
        obj_id tusken01 = create.object("tusken_executioner", tusken01Location);
        attachScript(tusken01, "city.bestine.tusken_spawner");
        setObjVar(tusken01, "spawner", self);
        tuskenSpawn.put("count", count + 1);
        location tusken02Location = new location(1.20f, 8.15f, -20.20f, "tatooine", getCellId(building, "r5"));
        obj_id tusken02 = create.object("tusken_observer", tusken02Location);
        attachScript(tusken02, "city.bestine.tusken_spawner");
        setObjVar(tusken02, "spawner", self);
        tuskenSpawn.put("count", count + 1);
        location tusken03Location = new location(-1.54f, 1.58f, -0.90f, "tatooine", getCellId(building, "r2"));
        obj_id tusken03 = create.object("tusken_observer", tusken03Location);
        attachScript(tusken03, "city.bestine.tusken_spawner");
        setObjVar(tusken03, "spawner", self);
        tuskenSpawn.put("count", count + 1);
        location tusken04Location = new location(3.07f, 8.31f, -39.26f, "tatooine", getCellId(building, "r5"));
        obj_id tusken04 = create.object("tusken_observer", tusken04Location);
        attachScript(tusken04, "city.bestine.tusken_spawner");
        setObjVar(tusken04, "spawner", self);
        tuskenSpawn.put("count", count + 1);
        location tusken05Location = new location(2.17f, 8.36f, -32.02f, "tatooine", getCellId(building, "r5"));
        obj_id tusken05 = create.object("tusken_witch_doctor", tusken05Location);
        attachScript(tusken05, "city.bestine.tusken_spawner");
        setObjVar(tusken05, "spawner", self);
        tuskenSpawn.put("count", count + 1);
        return SCRIPT_CONTINUE;
    }
    public int doDeathRespawn(obj_id self, dictionary params) throws InterruptedException
    {
        deltadictionary tuskenSpawn = self.getScriptVars();
        int count = tuskenSpawn.getInt("count");
        tuskenSpawn.put("count", count - 1);
        messageTo(self, "checkSpawnStatus", null, 1, false);
        return SCRIPT_CONTINUE;
    }
}
