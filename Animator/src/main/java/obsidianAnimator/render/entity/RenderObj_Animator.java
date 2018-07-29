package obsidianAnimator.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.PartRotation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderObj_Animator extends RenderLiving
{
    private ModelObj_Animator modelObj;
    private ItemStack leftItem;
    private ItemStack shopItem;

    public RenderObj_Animator()
    {
        super(null, 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return modelObj.getTexture();
    }

    public void setModel(ModelObj_Animator model)
    {
        modelObj = model;
        mainModel = model;
    }

    public void setLeftItem(ItemStack leftItem)
    {
        this.leftItem = leftItem;
    }

    public ItemStack getLeftItem()
    {
        return leftItem;
    }

    public ItemStack getShopItem()
    {
        return shopItem;
    }

    public void setShopItem(ItemStack shopItem)
    {
        this.shopItem = shopItem;
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase entity, float f)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        ItemStack itemstack1 = entity.getHeldItem();

        float f4;


        if (itemstack1 != null)
        {
            GL11.glPushMatrix();

            //Basic model fixes
            GL11.glRotatef(ModelObj.initRotFix, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, ModelObj.offsetFixY, 0.0F);

            postRenderItem(itemstack1, modelObj.getPartObjFromName("armLwR"),
                           modelObj.getPartFromName("prop_trans"),
                           (PartRotation) modelObj.getPartFromName("prop_rot"),
                           modelObj.getPartFromName("prop_scale"));

            renderItem(entity, itemstack1);

            GL11.glPopMatrix();
        }

        if (leftItem != null)
        {
            GL11.glPushMatrix();

            GL11.glRotatef(ModelObj.initRotFix, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, ModelObj.offsetFixY, 0.0F);

            postRenderItem(leftItem, modelObj.getPartObjFromName("armLwL"),
                           modelObj.getPartFromName("prop_trans_l"),
                           (PartRotation) modelObj.getPartFromName("prop_rot_l"),
                           modelObj.getPartFromName("prop_scale_l"));


            renderItem(entity, leftItem);

            GL11.glPopMatrix();
        }

        if (shopItem != null)
        {
            for (int i = 1; i <= 3; i++)
            {
                ItemStack item = new ItemStack(i == 1 ? Items.apple : (i == 2 ? Item.getItemFromBlock(Blocks.dirt) : Items.stone_sword));
                GL11.glPushMatrix();

                GL11.glRotatef(ModelObj.initRotFix, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, ModelObj.offsetFixY, 0.0F);

                postRenderItem(item, modelObj.getPartObjFromName("armLwR"),
                               modelObj.getPartFromName("shop_prop_trans_" + i),
                               (PartRotation) modelObj.getPartFromName("shop_prop_rot_" + i),
                               modelObj.getPartFromName("shop_prop_scale_" + i));


                //renderItem(entity, shopItem);

                EntityItem entityitem = new EntityItem(entity.worldObj, 0.0, 0.0, 0.0, item);
                entityitem.getEntityItem().stackSize = 1;
                entityitem.hoverStart = 0.0f;

                //GL11.glScalef(2f,2f,2f);
                //GL11.glPushMatrix();
                //GL11.glRotatef(90, 1f, 0f, 0f);
                //RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f);
                GL11.glScalef(0.25f,0.25f,0.25f);
                renderManager.itemRenderer.renderItem(entity,item,0, ItemRenderType.ENTITY);
                //GL11.glPopMatrix();

                GL11.glPopMatrix();
            }
        }

    }

    private void renderItem(EntityLivingBase entity, ItemStack itemstack1)
    {
        float f4;
        float f3;
        int k;
        float f12;

        if (itemstack1.getItem().requiresMultipleRenderPasses())
        {
            for (k = 0; k < itemstack1.getItem().getRenderPasses(itemstack1.getItemDamage()); ++k)
            {
                int i = itemstack1.getItem().getColorFromItemStack(itemstack1, k);
                f12 = (float) (i >> 16 & 255) / 255.0F;
                f3 = (float) (i >> 8 & 255) / 255.0F;
                f4 = (float) (i & 255) / 255.0F;
                GL11.glColor4f(f12, f3, f4, 1.0F);
                this.renderManager.itemRenderer.renderItem(entity, itemstack1, k, ItemRenderType.ENTITY);
            }
        } else
        {
            k = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
            float f11 = (float) (k >> 16 & 255) / 255.0F;
            f12 = (float) (k >> 8 & 255) / 255.0F;
            f3 = (float) (k & 255) / 255.0F;
            GL11.glColor4f(f11, f12, f3, 1.0F);
            this.renderManager.itemRenderer.renderItem(entity, itemstack1, 0, ItemRenderType.ENTITY);
        }
    }


    public void transformToItemCentreRight(ItemStack itemstack)
    {
        transformToItemCentre(itemstack, modelObj.getPartObjFromName("armLwR"), modelObj.getPartFromName("prop_trans"));
    }

    public void transformToItemCentreLeft(ItemStack itemstack)
    {
        transformToItemCentre(itemstack, modelObj.getPartObjFromName("armLwL"), modelObj.getPartFromName("prop_trans_l"));
    }

    public void transformToItemCentreShop(ItemStack itemstack, Part part)
    {
        String name = part.getName();
        transformToItemCentre(itemstack,
                              modelObj.getPartObjFromName("armLwR"),
                              modelObj.getPartFromName("shop_prop_trans_" + name.substring(name.lastIndexOf('_') + 1)));
    }

    /**
     * Transform an existing GL11 matrix to the held item location.
     * Takes prop translation into account.
     */
    public void transformToItemCentre(ItemStack itemstack, PartObj arm, Part propTrans)
    {
        if (arm == null)
            return;

        //Post render for lower right arm.
        arm.postRenderAll();

        //Prop translation
        float[] propTranslation = propTrans.getValues();
        GL11.glTranslatef(propTranslation[0], propTranslation[1], propTranslation[2]);

        if (itemstack != null)
        {
            if (propTrans.getName().contains("shop_prop"))
            {
                //GL11.glTranslatef(0, -0.30F, 0.2f);
                return;
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, ItemRenderType.EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, itemstack, ItemRendererHelper.BLOCK_3D));

            if (is3D || itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
                GL11.glTranslatef(0, -0.30F, 0.3f);
            else if (itemstack.getItem() == Items.bow)
                GL11.glTranslatef(-0.125f, -0.17F, 0.0f);
            else if (itemstack.getItem().isFull3D())
                GL11.glTranslatef(0.00f, -0.22F, 0.04f);
            else if (arm.getName().equalsIgnoreCase("armLwL")) // TODO: let the item decide this
                GL11.glTranslatef(0.138f, -0.25f, 0.0f);
            else
                GL11.glTranslatef(0, -0.30F, 0.2f);
        }
    }

    public void transformToItemCentreAndRotateRight(ItemStack itemstack)
    {
        transformToItemCentreAndRotate(itemstack,
                                       modelObj.getPartObjFromName("armLwR"),
                                       modelObj.getPartFromName("prop_trans"),
                                       (PartRotation) modelObj.getPartFromName("prop_rot"));
    }

    public void transformToItemCentreAndRotateLeft(ItemStack itemstack)
    {
        transformToItemCentreAndRotate(itemstack,
                                       modelObj.getPartObjFromName("armLwL"),
                                       modelObj.getPartFromName("prop_trans_l"),
                                       (PartRotation) modelObj.getPartFromName("prop_rot_l"));
    }

    public void transformToItemCentreAndRotateShop(ItemStack itemstack, Part part)
    {
        String name = part.getName();
        String number = name.substring(name.lastIndexOf('_') + 1);

        transformToItemCentreAndRotate(itemstack,
                                       modelObj.getPartObjFromName("armLwR"),
                                       modelObj.getPartFromName("shop_prop_trans_" + number),
                                       (PartRotation) modelObj.getPartFromName("shop_prop_rot_" + number));
    }

    /**
     * Transform an existing GL11 matrix to the held item location.
     * Takes prop rotation and translation into account.
     */
    public void transformToItemCentreAndRotate(ItemStack itemstack, PartObj arm, Part propTrans, PartRotation propRot)
    {

        //Prop rotation. Need to swap signs so rotation is the correct way.
        //System.out.println(prop_rot.getValue(0));
        propRot.setValue(-propRot.getValue(1), 1);
        propRot.setValue(-propRot.getValue(2), 2);

        transformToItemCentre(itemstack, arm, propTrans);
        propRot.rotate();

        //Need to swap back to original value.
        propRot.setValue(-propRot.getValue(1), 1);
        propRot.setValue(-propRot.getValue(2), 2);
    }

    /**
     * Transform an existing GL11 matrix for the player item.
     * Transforms to item centre, then does further transforms based on item (default MC transforms)
     */
    public void postRenderItem(ItemStack itemstack, PartObj arm, Part propTrans, PartRotation propRot, Part propScale)
    {
        float f2;

        transformToItemCentreAndRotate(itemstack, arm, propTrans, propRot);

        float[] scaleVals = propScale.getValues();

        if (itemstack != null)
        {
            if (propTrans.getName().contains("shop_prop"))
            {
                if (!(itemstack.getItem() instanceof ItemBlock))
                {
                    f2 = 0.375F;

                    //Prop scale
                    GL11.glScalef(1.0f + scaleVals[0], 1.0f + scaleVals[1], 1.0f + scaleVals[2]);

                    //GL11.glTranslatef(-0.2f, 0.02f, -0.1f);
                    GL11.glTranslatef(-0.1f, 0f, -0.1f);
                    //GL11.glScalef(f2, f2, f2);
                    GL11.glRotatef(88, 1, 0, 0);
                    GL11.glRotatef(130, 0, 1, 0);
                    GL11.glRotatef(26, 1, 0, 1);
                }

                return;
            }
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, ItemRenderType.EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, itemstack, ItemRendererHelper.BLOCK_3D));

            if (is3D || itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
            {
                f2 = 0.5F;
                f2 *= 0.75F;

                GL11.glTranslatef(0, 0.30F, -0.3f);
                GL11.glTranslatef(0f, -0.3f, 0.3f);

                //Prop scale
                GL11.glScalef(1.0f + scaleVals[0], 1.0f + scaleVals[1], 1.0f + scaleVals[2]);

                GL11.glRotatef(45, 1, 0, 0);
                GL11.glRotatef(45, 0f, 1f, 0f);
                GL11.glRotatef(180, 1f, 0f, 0f);
                GL11.glRotatef(-90, 0f, 1f, 0f);
                GL11.glScalef(-f2, -f2, f2);

            } else if (itemstack.getItem() == Items.bow)
            {
                f2 = 0.625F;

                //TODO postRenderItem: bow - adjust all this
                GL11.glTranslatef(0.125f, 0.17F, 0f);
                GL11.glTranslatef(0.125f, -0.4f, -0.25f);
                GL11.glScalef(f2, -f2, f2);

                //Prop scale
                GL11.glScalef(1.0f + scaleVals[0], 1.0f + scaleVals[1], 1.0f + scaleVals[2]);

                GL11.glRotatef(90f, 1f, 0f, 0f);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (itemstack.getItem().isFull3D())
            {
                f2 = 0.625F;

                //Prop scale
                GL11.glScalef(1.0f + scaleVals[0], 1.0f + scaleVals[1], 1.0f + scaleVals[2]);

                GL11.glTranslatef(0, 0.22F, -0.04f);
                GL11.glTranslatef(0f, -0.15F, 0f);

                GL11.glTranslatef(0.03f, 0f, 0f);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(70.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
            } else
            {
                f2 = 0.375F;

                //Prop scale
                GL11.glScalef(1.0f + scaleVals[0], 1.0f + scaleVals[1], 1.0f + scaleVals[2]);

                GL11.glTranslatef(-0.2f, 0.02f, -0.1f);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(88, 1, 0, 0);
                GL11.glRotatef(130, 0, 1, 0);
                GL11.glRotatef(26, 1, 0, 1);
            }
        }


    }

}
