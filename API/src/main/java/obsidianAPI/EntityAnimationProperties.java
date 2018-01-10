package obsidianAPI;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.ActionPointCallback;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EntityAnimationProperties implements IExtendedEntityProperties
{
    private final List<ActionPointCallback> actionPointCallbacks = Lists.newLinkedList();
    private Entity entity;
    private String entityName;
    private AnimationSequence activeAnimation;
    private long animationStartTime;
    private boolean loop;
    private float multiplier = 1f;

    private long now;

    private int nextFrame = 0;

    private float prevEntityPosX;
    private float prevEntityPosZ;

    private Runnable onFinished;

    private float frameTime = 0f;

    @Override
    public void init(Entity entity, World world)
    {
        this.entity = entity;
        entityName = AnimationRegistry.getEntityName(entity.getClass());
    }

    public void addActionPointCallback(ActionPointCallback callback)
    {
        actionPointCallbacks.add(callback);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {

    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {

    }

    public void updateFrameTime()
    {
        now = System.nanoTime();

        if (activeAnimation == null)
            frameTime = 0f;
        else
            frameTime = Util.getAnimationFrameTime(now, animationStartTime, 0, activeAnimation.getFPS(), multiplier);
    }

    public void setActiveAnimation(ModelObj model, String binding, boolean loop)
    {
        setActiveAnimation(model, binding, loop, 0.25f);
    }

    public void setActiveAnimation(ModelObj model, String binding, boolean loopAnim, float transitionTime)
    {
        Map<String, float[]> currentValues;
        if (activeAnimation != null)
        {
            currentValues = getCurrentValues(model);
        } else
        {
            currentValues = getOriginalValues(model);
        }

        multiplier = 1f;
        animationStartTime = now;
        nextFrame = 0;
        onFinished = null;

        prevEntityPosX = 0f;
        prevEntityPosZ = 0f;
        if (transitionTime > 0.001f)
        {
            loop = false;
            AnimationSequence next = AnimationRegistry.getAnimation(entityName, binding);

            activeAnimation = Util.createTransition(model, next.getName(), currentValues, next.getPartValuesAtTime(model, 0f), transitionTime);
            onFinished = () ->
            {
                animationStartTime = now;
                nextFrame = 0;
                onFinished = null;
                loop = loopAnim;
                activeAnimation = next;
            };
        } else
        {
            this.loop = loopAnim;
            activeAnimation = AnimationRegistry.getAnimation(entityName, binding);
        }
    }

    public void clearAnimation(ModelObj model, float transitionTime)
    {
        if (activeAnimation == null || !activeAnimation.getName().equals("Idle"))
        {
            setActiveAnimation(model, "Idle", true, transitionTime);
        }
    }

    public void clearAnimation(ModelObj model)
    {
        clearAnimation(model, 0.25f);
    }

    public void setMultiplier(float multiplier)
    {
        if (frameTime > 0)
        {
            animationStartTime = (long) (now - (now - animationStartTime) * (double) this.multiplier / (double) multiplier);
        }
        this.multiplier = multiplier;
    }

    private Map<String, float[]> getOriginalValues(ModelObj model)
    {
        Map<String, float[]> values = Maps.newHashMap();

        for (Part part : model.parts)
        {
            values.put(part.getName(), part.getOriginalValues());
        }

        return values;
    }

    private Map<String, float[]> getCurrentValues(ModelObj model)
    {
        Map<String, float[]> values = Maps.newHashMap();

        float time = getAnimationFrameTime();

        for (Part part : model.parts)
        {
            values.put(part.getName(), activeAnimation.getPartValueAtTime(part, time));
        }

        return values;
    }

    public float getAnimationFrameTime()
    {
        return frameTime;
    }

    public AnimationSequence getActiveAnimation()
    {
        return activeAnimation;
    }

    public void updateAnimation(ModelObj model, float time)
    {
        if (activeAnimation != null)
        {
            Part part = model.getPartFromName("entitypos");
            if (part != null)
            {
                float entityPosX = part.getValue(0);
                float entityPosZ = part.getValue(2);

                float strafe = entityPosX - prevEntityPosX;
                float forward = entityPosZ - prevEntityPosZ;

                float f4 = MathHelper.sin(entity.rotationYaw * (float) Math.PI / 180.0F);
                float f5 = MathHelper.cos(entity.rotationYaw * (float) Math.PI / 180.0F);
                entity.setPosition(entity.posX + (double) (strafe * f5 - forward * f4), entity.posY, entity.posZ + (double) (forward * f5 + strafe * f4));

                prevEntityPosX = entityPosX;
                prevEntityPosZ = entityPosZ;
            }

            while (time > nextFrame)
            {
                fireActions(nextFrame);
                nextFrame++;
            }

            if (time > activeAnimation.getTotalTime())
            {
                if (loop)
                {
                    setActiveAnimation(model, activeAnimation.getName(), true, 0f);
                } else if (onFinished != null)
                {
                    onFinished.run();
                } else
                {
                    clearAnimation(model);
                }
            }
        }
    }

    private void fireActions(int frame)
    {
        if (activeAnimation != null)
        {
            Collection<String> actions = activeAnimation.getActionPoints(frame);

            for (String action : actions)
            {
                for (ActionPointCallback callback : actionPointCallbacks)
                {
                    callback.onActionPoint(entity, action);
                }
            }
        }
    }
}
