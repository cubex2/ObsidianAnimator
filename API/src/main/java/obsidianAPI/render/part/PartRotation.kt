package obsidianAPI.render.part

import obsidianAPI.render.ModelObj
import org.lwjgl.opengl.GL11

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class PartRotation(modelObj: ModelObj, name: String) : Part(modelObj, name)
{
    private var rotationMatrix: FloatBuffer = setupRotationMatrix()

    private fun setupRotationMatrix(): FloatBuffer
    {
        val rotationMatrixF = FloatArray(16)
        val vbb = ByteBuffer.allocateDirect(rotationMatrixF.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        val rotationMatrix = vbb.asFloatBuffer()
        rotationMatrix.put(rotationMatrixF)
        rotationMatrix.position(0)

        return rotationMatrix
    }

    override fun rotate()
    {
        GL11.glRotated(valueX / Math.PI * 180f, 1.0, 0.0, 0.0)
        GL11.glRotated(valueY / Math.PI * 180f, 0.0, 1.0, 0.0)
        GL11.glRotated(valueZ / Math.PI * 180f, 0.0, 0.0, 1.0)
    }

    fun rotateLocal(delta: Float, dim: Int)
    {
        GL11.glPushMatrix()
        GL11.glLoadIdentity()
        rotate()
        when (dim)
        {
            0 -> GL11.glRotated(delta.toDouble(), 1.0, 0.0, 0.0)
            1 -> GL11.glRotated(delta.toDouble(), 0.0, 1.0, 0.0)
            2 -> GL11.glRotated(delta.toDouble(), 0.0, 0.0, 1.0)
        }

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, rotationMatrix!!)
        updateRotationAnglesFromMatrix()
        GL11.glPopMatrix()
    }

    private fun updateRotationAnglesFromMatrix()
    {
        val x: Double
        val y: Double
        val z: Double
        val r8 = rotationMatrix.get(8)
        if (Math.abs(r8) != 1f)
        {
            y = -Math.asin(r8.toDouble())
            val cy = Math.cos(y)

            //Find x value
            val r9 = rotationMatrix.get(9)
            val r10 = rotationMatrix.get(10)
            x = Math.atan2(r9 / cy, r10 / cy)

            //Find z value
            val r0 = rotationMatrix.get(0)
            val r4 = rotationMatrix.get(4)
            z = Math.atan2(r4 / cy, r0 / cy)
        } else
        {
            //Gimbal lock case - infinite solutions, set z to zero.
            z = 0.0
            val r1 = rotationMatrix.get(1)
            val r2 = rotationMatrix.get(2)
            if (r8 == -1f)
            {
                y = Math.PI / 2
                x = z + Math.atan2(r1.toDouble(), r2.toDouble())
            } else
            {
                y = -Math.PI / 2
                x = z + Math.atan2((-r1).toDouble(), (-r2).toDouble())
            }
        }

        valueX = (-x).toFloat()
        valueY = (-y).toFloat()
        valueZ = (-z).toFloat()
    }

}
