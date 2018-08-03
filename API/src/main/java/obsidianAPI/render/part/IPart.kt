package obsidianAPI.render.part

import org.lwjgl.opengl.GL11.glTranslatef

interface IPart
{
    val name: String

    fun getParentPart(): IPart?

    fun hasParent(): Boolean = getParentPart() != null

    fun getRotationPoint(axis: Int): Float

    fun rotate()

    /**
     * Complete post render - parent post render, translation for this part, then rotation for this part.
     */
    fun postRenderAll()
    {
        postRenderParent(createPartHierarchy())
    }

    /**
     * Returns the part hierarchy like this: [root, ..., parent, this]
     */
    fun createPartHierarchy(): List<IPart>
    {
        // root, ..., parent, this
        val partHierarchy = mutableListOf<IPart>()

        var part: IPart? = this
        do
        {
            partHierarchy.add(0, part!!)
            part = part.getParentPart()
        } while (part != null)

        return partHierarchy
    }

    fun postRenderParent(parentList: List<IPart>)
    {
        val totalTranslation = floatArrayOf(0f, 0f, 0f)

        parentList.forEach { p ->
            glTranslatef(
                    -p.getRotationPoint(0) - totalTranslation[0],
                    -p.getRotationPoint(1) - totalTranslation[1],
                    -p.getRotationPoint(2) - totalTranslation[2]
            )

            (0 until 3).forEach { totalTranslation[it] = -p.getRotationPoint(it) }

            p.rotate()
        }
    }

    fun move()
    {
        glTranslatef(
                -getRotationPoint(0),
                -getRotationPoint(1),
                -getRotationPoint(2)
        )
        rotate()
        glTranslatef(
                getRotationPoint(0),
                getRotationPoint(1),
                getRotationPoint(2)
        )
    }
}