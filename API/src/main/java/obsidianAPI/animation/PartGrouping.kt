package obsidianAPI.animation

import com.google.common.base.Joiner
import com.google.common.collect.HashMultimap
import com.google.common.collect.Maps
import obsidianAPI.data.ModelDefinition
import obsidianAPI.data.PartData
import obsidianAPI.render.part.Part

class PartGrouping(private val modelDefinition: ModelDefinition)
{
    private val groups = hashSetOf<String>()
    private val groupToParts = HashMultimap.create<String, String>()
    private val partToGroup = Maps.newHashMap<String, String>()

    init
    {
        modelDefinition.partData.forEach {
            groups.add(it.group)
            groupToParts.put(it.group, it.partName)
            partToGroup[it.partName] = it.group
        }
    }

    fun getGroups() = groups

    fun addGroup(group: String)
    {
        groups.add(group)
    }

    fun setGroupForPart(group: String, part: Part)
    {
        groups.add(group)

        partToGroup[part.name]?.let { currentGroup -> groupToParts.remove(currentGroup, part.name) }

        groupToParts.put(group, part.name)
        partToGroup[part.name] = group

        modelDefinition.partData.removeIf { it.partName == part.name }
        modelDefinition.partData.add(PartData(part.name, part.displayName, group))
    }

    fun getGroup(part: Part): String?
    {
        return partToGroup[part.name]
    }

    fun getGroupListString(): String
    {
        return Joiner.on(", ").join(groups)
    }
}