package obsidianAnimator.gui.entityRenderer;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import obsidianAPI.render.part.*;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.render.MathHelper;
import obsidianAnimator.render.RayTrace;
import org.lwjgl.opengl.GL11;

public class GuiEntityRendererWithTranslation extends GuiEntityRendererWithRotation
{

    private boolean translationAxisMouseOver = false;
    private boolean translationAxisDrag = false;
    private Integer translationAxisPlane;

    //Vector for current rotation of wheel while dragging.
    private Vec3 translationGuidePoint;
    //Vector for rotation of wheel when first clicked on.
    private Vec3 initialTranslationGuidePoint;
    private double prevTranslationDelta = 0.0F;

    public GuiEntityRendererWithTranslation(TimelineController controller)
    {
        super(controller);
    }

    /* ---------------------------------------------------- *
     * 						Input							*
     * ---------------------------------------------------- */

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        //If mouse over and lmb clicked, begin drag.
        if (translationAxisMouseOver && button == 0)
            translationAxisDrag = true;
        else
            super.mouseClicked(x, y, button);
    }

    @Override
    public void mouseMovedOrUp(int x, int y, int which)
    {
        super.mouseMovedOrUp(x, y, which);
        //If lmb lifted, reset drag, guide and delta.
        if (which == 0)
        {
            if (translationAxisDrag)
                onControllerRelease();
            translationGuidePoint = null;
            translationAxisDrag = false;
            prevTranslationDelta = 0.0F;
        }
    }

    /* ---------------------------------------------------- *
     * 					  Ray Trace							*
     * ---------------------------------------------------- */

    @Override
    public void processRay()
    {
        if (selectedPart != null)
        {
            GL11.glPushMatrix();

            if (selectedPart instanceof PartRotation)
            {
                GL11.glPopMatrix();
                super.processRay();
                return;
            } else if (selectedPart instanceof PartPropTranslation || selectedPart instanceof PartPropScale)
            {
                ItemStack itemstack = entityToRender.getHeldItem();
                if (selectedPart.getName().equals("prop_trans_l") || selectedPart.getName().equals("prop_scale_l"))
                {
                    ModelHandler.modelRenderer.transformToItemCentreLeft(ModelHandler.modelRenderer.getLeftItem());
                } else if (selectedPart.getName().startsWith("shop_prop"))
                {
                    ModelHandler.modelRenderer.transformToItemCentreShop(ModelHandler.modelRenderer.getShopItem(), selectedPart);
                } else
                {
                    ModelHandler.modelRenderer.transformToItemCentreRight(itemstack);
                }
            }


            drawTranslationAxis();

            translationAxisMouseOver = false;
            if (!translationAxisDrag)
            {
                updateAxisMouseOver();
                //If it isn't being moused over, hand over ray processing to GuiEntityRenderer, which will test for hovering over parts.
                if (!translationAxisMouseOver)
                {
                    GL11.glPopMatrix();
                    super.processRay();
                    return;
                }
                //If it is being hovered over, ensure there is no part highlighted for selection.
                else
                    hoveredPart = null;
            } else
                onControllerDrag();

            GL11.glPopMatrix();
        } else
            super.processRay();
    }

    private void updateAxisMouseOver()
    {
        Integer dim = testAxisRay();
        if (dim != null)
        {
            GL11.glPushMatrix();
            Part part = selectedPart;
            if (part instanceof PartEntityPos)
                GL11.glTranslated(-entityToRender.posX, -entityToRender.posY, -entityToRender.posZ);
            translationAxisMouseOver = true;
            translationAxisPlane = dim;
            int i = translationAxisPlane == 0 ? 2 : translationAxisPlane - 1;
            translationGuidePoint = getMouseVectorInPlane(i);
            initialTranslationGuidePoint = translationGuidePoint;
            Vec3 v = null;
            switch (translationAxisPlane)
            {
                case 0:
                    v = Vec3.createVectorHelper(1, 0, 0);
                    break;
                case 1:
                    v = Vec3.createVectorHelper(0, 1, 0);
                    break;
                case 2:
                    v = Vec3.createVectorHelper(0, 0, 1);
                    break;
            }
            if (translationGuidePoint != null)
                prevTranslationDelta = MathHelper.getLineScalarForClosestPoint(Vec3.createVectorHelper(0, 0, 0), v, translationGuidePoint);
            GL11.glPopMatrix();
        } else
            translationAxisPlane = null;
    }

    public Integer testAxisRay()
    {
        Double min = null;
        Integer dim = null;
        for (int i = 0; i < 3; i++)
        {
            Vec3 p = null;
            Vec3 n = null;
            switch (i)
            {
                case 0:
                    p = Vec3.createVectorHelper(MathHelper.rotationWheelRadius, 0, 0);
                    n = Vec3.createVectorHelper(0, 1, 0);
                    break;
                case 1:
                    p = Vec3.createVectorHelper(0, MathHelper.rotationWheelRadius, 0);
                    n = Vec3.createVectorHelper(0, 0, 1);
                    break;
                case 2:
                    p = Vec3.createVectorHelper(0, 0, MathHelper.rotationWheelRadius);
                    n = Vec3.createVectorHelper(1, 0, 0);
                    break;
            }
            Double d = MathHelper.rayIntersectsAxisSlider(RayTrace.getRayTrace(), p, n);
            if (d != null && (min == null || d < min))
            {
                min = d;
                dim = i;
            }
        }
        return dim;
    }

    private void processAxisDrag()
    {
        if (translationGuidePoint != null && initialTranslationGuidePoint != null)
        {
            Vec3 v = null;
            switch (translationAxisPlane)
            {
                case 0:
                    v = Vec3.createVectorHelper(1, 0, 0);
                    break;
                case 1:
                    v = Vec3.createVectorHelper(0, 1, 0);
                    break;
                case 2:
                    v = Vec3.createVectorHelper(0, 0, 1);
                    break;
            }

            double translationDelta = MathHelper.getLineScalarForClosestPoint(Vec3.createVectorHelper(0, 0, 0), v, translationGuidePoint);
            double d = translationDelta - prevTranslationDelta;
            if (!Double.isNaN(d))
            {
                updatePartValue(d, translationAxisPlane);
                if (selectedPart instanceof PartEntityPos || selectedPart instanceof PartPropScale)
                    prevTranslationDelta = translationDelta;
                else
                    prevTranslationDelta = translationDelta - d;
            }
        }
    }

    @Override
    protected void onControllerDrag()
    {
        Part part = selectedPart;
        if (part instanceof PartObj || part instanceof PartRotation)
            super.onControllerDrag();
        else
        {
            GL11.glPushMatrix();
            if (part instanceof PartEntityPos)
                GL11.glTranslated(-entityToRender.posX, -entityToRender.posY, -entityToRender.posZ);
            int i = translationAxisPlane == 0 ? 2 : translationAxisPlane - 1;
            translationGuidePoint = getMouseVectorInPlane(i);
            processAxisDrag();
            GL11.glPopMatrix();
        }
    }

    @Override
    protected void onControllerRelease()
    {
        super.onControllerRelease();
    }

    /* ---------------------------------------------------- *
     * 						Render							*
     * ---------------------------------------------------- */

    private void drawTranslationAxis()
    {
        Vec3 origin = Vec3.createVectorHelper(0.0F, 0.0F, 0.0F);
        int colour = 0xFFFFFF;
        Vec3 v = null;
        for (int i = 0; i < 3; i++)
        {
            switch (i)
            {
                case 0:
                    colour = 0xFF0000;
                    v = Vec3.createVectorHelper(MathHelper.rotationWheelRadius, 0.0F, 0.0F);
                    break;
                case 1:
                    colour = 0x00FF00;
                    v = Vec3.createVectorHelper(0.0F, MathHelper.rotationWheelRadius, 0.0F);
                    break;
                case 2:
                    colour = 0x0000FF;
                    v = Vec3.createVectorHelper(0.0F, 0.0F, MathHelper.rotationWheelRadius);
                    break;
            }
            if (translationAxisPlane != null && translationAxisPlane == i)
                drawLine(origin, v, colour, 3.0F, 1.0F);
            else
                drawLine(origin, v, colour, 2.0F, 0.4F);
        }
    }

    private void drawLine(Vec3 p1, Vec3 p2, int color, float width, float alpha)
    {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        GL11.glLineWidth(width);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawing(1);
        tessellator.setColorRGBA_I(color, (int) (alpha * 255));
        tessellator.addVertex(p1.xCoord, p1.yCoord, p1.zCoord);
        tessellator.addVertex(p2.xCoord, p2.yCoord, p2.zCoord);
        tessellator.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

}
