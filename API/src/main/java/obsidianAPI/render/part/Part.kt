package obsidianAPI.render.part

import obsidianAPI.render.ModelObj

/**
 * An abstract object for tracking information about a part (limb, position of model etc).
 */
abstract class Part(@JvmField val modelObj: ModelObj?, internalName: String) : IPart
{
    @JvmField
    protected var valueX: Float = 0.toFloat()
    @JvmField
    protected var valueY: Float = 0.toFloat()
    @JvmField
    protected var valueZ: Float = 0.toFloat()
    var originalValues: FloatArray
    //------------------------------------------
    //  			Basics
    //------------------------------------------

    val internalName: String = internalName.toLowerCase()

    override val name: String
        get() = internalName

    var values: FloatArray
        get() = floatArrayOf(valueX, valueY, valueZ)
        set(values)
        {
            valueX = values[0]
            valueY = values[1]
            valueZ = values[2]
        }

    init
    {
        valueX = 0.0f
        valueY = 0.0f
        valueZ = 0.0f
        originalValues = floatArrayOf(0.0f, 0.0f, 0.0f)
    }

    fun setValue(f: Float, i: Int)
    {
        when (i)
        {
            0 -> valueX = f
            1 -> valueY = f
            2 -> valueZ = f
        }
    }

    fun getValue(i: Int): Float
    {
        when (i)
        {
            0 -> return valueX
            1 -> return valueY
            2 -> return valueZ
        }
        return 0.0f
    }

    fun setToOriginalValues()
    {
        values = originalValues
    }

    override fun getParentPart(): IPart? = null

    override fun getRotationPoint(axis: Int): Float = 0f

    override fun rotate()
    {
        // NO OP
    }

    override fun equals(o: Any?): Boolean
    {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val part = o as Part?

        return if (internalName != part!!.internalName) false else modelObj == part.modelObj
    }

    override fun hashCode(): Int
    {
        var result = internalName.hashCode()
        result = 31 * result + (modelObj?.hashCode() ?: 0)
        return result
    }
}
