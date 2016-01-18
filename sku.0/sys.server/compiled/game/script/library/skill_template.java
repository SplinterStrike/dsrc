package script.library;

import script.*;
import script.base_class.*;
import script.combat_engine.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import script.base_script;

import script.library.utils;

public class skill_template extends script.base_script
{
    public skill_template()
    {
    }
    public static final float NON_TEMPLATE_XP_RATIO = 1.0f;
    public static final float QUEST_XP_RATIO = 1.0f;
    public static final String NO_TEMPLATE_STARTING = "new_character_no_skill";
    public static final String TEMPLATE_TABLE = "datatables/skill_template/skill_template.iff";
    public static final String ITEM_REWARD_TABLE = "datatables/roadmap/item_rewards.iff";
    public static String[] getSkillTemplateSkillsByTemplateName(String templateName) throws InterruptedException
    {
        if (templateName == null || templateName.length() <= 0)
        {
            return null;
        }
        String template = dataTableGetString(TEMPLATE_TABLE, templateName, "template");
        if (template == null || template.equals(""))
        {
            return null;
        }
        return split(template, ',');
    }
    public static String[] getSkillTemplateSkills(obj_id player) throws InterruptedException
    {
        if (!isIdValid(player))
        {
            return null;
        }
        String templateName = getSkillTemplate(player);
        return getSkillTemplateSkillsByTemplateName(templateName);
    }
    public static boolean isValidWorkingSkill(String skillName) throws InterruptedException
    {
        if (skillName == null || skillName.equals(""))
        {
            return false;
        }
        return true;
    }
    public static String getNextWorkingSkill(obj_id player) throws InterruptedException
    {
        if (!isIdValid(player))
        {
            return null;
        }
        String[] template = getSkillTemplateSkills(player);
        if (template == null || template.length == 0)
        {
            return null;
        }
        String skillName = getWorkingSkill(player);
        if (!isValidWorkingSkill(skillName))
        {
            setWorkingSkill(player, template[0]);
            return template[0];
        }
        int idx = utils.getElementPositionInArray(template, skillName);
        idx++;
        if (idx >= template.length)
        {
            return null;
        }
        return template[idx];
    }
    public static String getTemplateSkillXpType(obj_id player, boolean verbose) throws InterruptedException
    {
        if (!isIdValid(player))
        {
            return null;
        }
        int loopCount = utils.getIntScriptVar(player, "skillTemplate.loop");
        loopCount++;
        String skillName = getWorkingSkill(player);
        String skillTemplate = getSkillTemplate(player);
        if (loopCount > 100)
        {
            logScriptDataError("Infinite skill template loop (template=" + skillTemplate + " ;working skill=" + skillName + ")");
            return null;
        }
        if (!isValidWorkingSkill(skillName))
        {
            if ((skillName != null && skillName.equals(NO_TEMPLATE_STARTING)) && (skillTemplate == null || skillTemplate.equals("")))
            {
                utils.removeScriptVar(player, "skillTemplate.loop");
                return skillName;
            }
            skillName = getNextWorkingSkill(player);
            if (!isValidWorkingSkill(skillName))
            {
                if (verbose)
                {
                    sendSystemMessage(player, new string_id("base_player", "skill_template_no_xp_nag"));
                }
                utils.removeScriptVar(player, "skillTemplate.loop");
                return null;
            }
            else 
            {
                setWorkingSkill(player, skillName);
            }
        }
        if (hasSkill(player, skillName))
        {
            if (skillTemplate == null || skillTemplate.equals(""))
            {
                if (verbose)
                {
                    sendSystemMessage(player, new string_id("base_player", "skill_template_old_xp_nag"));
                }
            }
            else 
            {
                skillName = getNextWorkingSkill(player);
                if (isValidWorkingSkill(skillName))
                {
                    setWorkingSkill(player, skillName);
                    utils.setScriptVar(player, "skillTemplate.loop", loopCount);
                    return getTemplateSkillXpType(player, false);
                }
            }
        }
        String templateCombatXpType = getSkillExperienceType(skillName);
        if (templateCombatXpType == null || templateCombatXpType.equals(""))
        {
            if (earnWorkingSkill(player))
            {
                utils.setScriptVar(player, "skillTemplate.loop", loopCount);
                return getTemplateSkillXpType(player, false);
            }
            utils.removeScriptVar(player, "skillTemplate.loop");
            return null;
        }
        utils.removeScriptVar(player, "skillTemplate.loop");
        return templateCombatXpType;
    }
    public static String getSkillExperienceType(String skillName) throws InterruptedException
    {
        dictionary xpReqs = getSkillPrerequisiteExperience(skillName);
        if (xpReqs == null || xpReqs.isEmpty())
        {
            return null;
        }
        java.util.Enumeration e = xpReqs.keys();
        String xpType = (String)(e.nextElement());
        return xpType;
    }
    public static boolean isQualifiedForWorkingSkill(obj_id player) throws InterruptedException
    {
        if (!isIdValid(player))
        {
            return false;
        }
        String skillName = getWorkingSkill(player);
        if (!isValidWorkingSkill(skillName))
        {
            return false;
        }
        if (hasSkill(player, skillName))
        {
            return false;
        }
        dictionary xpReqs = getSkillPrerequisiteExperience(skillName);
        if (xpReqs == null || xpReqs.isEmpty())
        {
            return true;
        }
        java.util.Enumeration e = xpReqs.keys();
        String xpType = (String)(e.nextElement());
        int xpCost = xpReqs.getInt(xpType);
        if (getExperiencePoints(player, xpType) < xpCost)
        {
            return false;
        }
        return true;
    }
    public static boolean earnWorkingSkill(obj_id player) throws InterruptedException
    {
        if (!isIdValid(player))
        {
            return false;
        }
        String skillName = getWorkingSkill(player);
        if (!isValidWorkingSkill(skillName))
        {
            return false;
        }
        if (skill.purchaseSkill(player, skillName))
        {
            grantRoadmapItem(player);
            setWorkingSkill(player, getNextWorkingSkill(player));
            return true;
        }
        return false;
    }
    public static void validateWorkingSkill(obj_id player) throws InterruptedException
    {
        String[] skillList = getSkillTemplateSkills(player);
        if (skillList == null || skillList.length == 0)
        {
            return;
        }
        for (int i = 0; i < skillList.length; i++)
        {
            if (!hasSkill(player, skillList[i]))
            {
                setWorkingSkill(player, skillList[i]);
                break;
            }
        }
    }
    public static boolean grantRoadmapItem(obj_id player) throws InterruptedException
    {
        String skillName = getWorkingSkill(player);
        if (!isValidWorkingSkill(skillName))
        {
            return false;
        }
        String skillTemplate = getSkillTemplate(player);
        if (skillTemplate == null || skillTemplate.equals(""))
        {
            return false;
        }
        String itemGrant = getRoadmapItem(player, skillTemplate, skillName);
        if (itemGrant == null || itemGrant.equals(""))
        {
            return false;
        }
        String[] items = split(itemGrant, ',');
        if (items == null || items.length == 0)
        {
            return false;
        }
        Vector allNewObjectsResizable = new Vector();
        allNewObjectsResizable.setSize(0);
        boolean success = true;
        for (int i = 0; i < items.length; i++)
        {
            obj_id newItem = null;
            if (items[i].endsWith(".iff"))
            {
                newItem = createObjectInInventoryAllowOverload(items[i], player);
            }
            else 
            {
                newItem = static_item.createNewItemFunction(items[i], player);
            }
            if (!isIdValid(newItem))
            {
                LOG("roadmap", "ERROR - Could not create roadmap item (" + items[i] + ")");
                success = false;
            }
            else 
            {
                utils.addElement(allNewObjectsResizable, newItem);
            }
        }
        string_id itemDesc = utils.unpackString(getRoadmapItemDesc(skillTemplate, skillName));
        prose_package pp = prose.getPackage(new string_id("base_player", "skill_template_item_reward"), itemDesc);
        sendSystemMessageProse(player, pp);
        obj_id[] allNewObjects = new obj_id[0];
        if (allNewObjectsResizable != null)
        {
            allNewObjects = new obj_id[allNewObjectsResizable.size()];
            allNewObjectsResizable.toArray(allNewObjects);
        }
        showLootBox(player, allNewObjects);
        return success;
    }
    public static String getRoadmapItemDesc(String skillTemplate, String workingSkill) throws InterruptedException
    {
        int row = dataTableSearchColumnForString(skillTemplate, 0, ITEM_REWARD_TABLE);
        if (row < 0)
        {
            return null;
        }
        String[] templateNames = dataTableGetStringColumn(ITEM_REWARD_TABLE, "roadmapTemplateName");
        String[] templateSkills = dataTableGetStringColumn(ITEM_REWARD_TABLE, "roadmapSkillName");
        for (int i = row; i < templateSkills.length; i++)
        {
            if (!templateNames[i].equals(skillTemplate))
            {
                break;
            }
            if (templateSkills[i].equals(workingSkill))
            {
                String itemDesc = dataTableGetString(ITEM_REWARD_TABLE, i, "stringId");
                if (itemDesc != null && !itemDesc.equals(""))
                {
                    return itemDesc;
                }
            }
        }
        return "";
    }
    public static String getRoadmapItem(obj_id player, String skillTemplate, String workingSkill) throws InterruptedException
    {
        int row = dataTableSearchColumnForString(skillTemplate, 0, ITEM_REWARD_TABLE);
        if (row < 0)
        {
            return null;
        }
        String[] templateNames = dataTableGetStringColumn(ITEM_REWARD_TABLE, "roadmapTemplateName");
        String[] templateSkills = dataTableGetStringColumn(ITEM_REWARD_TABLE, "roadmapSkillName");
        for (int i = row; i < templateSkills.length; i++)
        {
            if (!templateNames[i].equals(skillTemplate))
            {
                break;
            }
            if (templateSkills[i].equals(workingSkill))
            {
                String defaultItem = dataTableGetString(ITEM_REWARD_TABLE, i, "itemDefault");
                int species = getSpecies(player);
                if (species == SPECIES_WOOKIEE)
                {
                    String wookieeItem = dataTableGetString(ITEM_REWARD_TABLE, i, "itemWookiee");
                    if (wookieeItem != null && !wookieeItem.equals(""))
                    {
                        return wookieeItem;
                    }
                }
                else if (species == SPECIES_ITHORIAN)
                {
                    String ithorianItem = dataTableGetString(ITEM_REWARD_TABLE, i, "itemIthorian");
                    if (ithorianItem != null && !ithorianItem.equals(""))
                    {
                        return ithorianItem;
                    }
                }
                if (defaultItem != null && !defaultItem.equals(""))
                {
                    return defaultItem;
                }
            }
        }
        return null;
    }
}