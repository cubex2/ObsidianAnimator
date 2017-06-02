package ru.gloomyfolken.tcn2obj.qubble;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Credit: LLibrary
 *
 * @author iLexiconn
 */
public class QubbleModel
{
	private static final int QUBBLE_VERSION = 1;

	private String name;
	private String author;
	private int version;
	private int textureWidth = 64;
	private int textureHeight = 32;
	private List<QubbleCuboid> cuboids = new ArrayList<QubbleCuboid>();
	private transient String fileName;

	private Map<String, String> textures = new LinkedHashMap<String, String>();

	private QubbleModel()
	{
	}

	public static QubbleModel create(String name, String author, int textureWidth, int textureHeight)
	{
		QubbleModel model = new QubbleModel();
		model.setName(name == null ? "Unknown" : name);
		model.setAuthor(author == null ? "Unknown" : author);
		model.setTextureWidth(textureWidth);
		model.setTextureHeight(textureHeight);
		model.setVersion(QUBBLE_VERSION);
		return model;
	}

	public static QubbleModel deserialize(NBTTagCompound compound)
	{
		QubbleModel model = new QubbleModel();
		model.deserializeNBT(compound);
		return model;
	}

	public void deserializeNBT(NBTTagCompound compound)
	{
		this.name = compound.getString("name");
		this.author = compound.getString("author");
		this.version = compound.getInteger("version");

		if (compound.hasKey("texture"))
		{
			NBTTagCompound textureTag = compound.getCompoundTag("texture");
			this.textureWidth = textureTag.getInteger("width");
			this.textureHeight = textureTag.getInteger("height");
		}

		this.cuboids.clear();

		NBTTagList cuboidTag = compound.getTagList("cuboids", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < cuboidTag.tagCount(); i++)
		{
			this.cuboids.add(QubbleCuboid.deserialize(cuboidTag.getCompoundTagAt(i)));
		}

		if (compound.hasKey("textures"))
		{
			NBTTagList textures = compound.getTagList("textures", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < textures.tagCount(); i++)
			{
				NBTTagCompound texture = textures.getCompoundTagAt(i);

				if (texture.hasKey("key") && texture.hasKey("value"))
				{
					this.textures.put(texture.getString("key"), texture.getString("value"));
				}
			}
		}
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFileName()
	{
		return this.fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getAuthor()
	{
		return this.author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public int getVersion()
	{
		return this.version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public int getTextureWidth()
	{
		return this.textureWidth;
	}

	public void setTextureWidth(int textureWidth)
	{
		this.textureWidth = textureWidth;
	}

	public int getTextureHeight()
	{
		return this.textureHeight;
	}

	public void setTextureHeight(int textureHeight)
	{
		this.textureHeight = textureHeight;
	}

	public List<QubbleCuboid> getCuboids()
	{
		return this.cuboids;
	}

	public String getTexture(String key)
	{
		return this.textures.get(key);
	}

	public void setTexture(String key, String value)
	{
		if (value == null)
		{
			this.textures.remove(key);
		}
		else
		{
			this.textures.put(key, value);
		}
	}

	public Map<String, String> getTextures()
	{
		return this.textures;
	}

	public void setTexture(int width, int height)
	{
		this.textureWidth = width;
		this.textureHeight = height;
	}

	public void removeCuboid(QubbleCuboid cuboid)
	{
		for (QubbleCuboid currentCuboid : this.getCuboids())
		{
			this.removeChildCuboid(currentCuboid, cuboid);
		}
	}

	private boolean removeChildCuboid(QubbleCuboid parent, QubbleCuboid cuboid)
	{
		boolean isChild = false;

		for (QubbleCuboid currentCuboid : parent.getChildren())
		{
			if (currentCuboid.equals(cuboid))
			{
				isChild = true;
				break;
			}

			if (this.removeChildCuboid(currentCuboid, cuboid))
			{
				return true;
			}
		}

		if (isChild)
		{
			parent.getChildren().remove(cuboid);
			return true;
		}

		return false;
	}

	public QubbleCuboid getCuboid(String name)
	{
		for (QubbleCuboid cuboid : this.cuboids)
		{
			if (cuboid.getName().equals(name))
			{
				return cuboid;
			}
			else
			{
				QubbleCuboid c = cuboid.getCuboid(name);

				if (c != null)
				{
					return c;
				}
			}
		}

		return null;
	}

	public QubbleCuboid getParent(QubbleCuboid cuboid)
	{
		for (QubbleCuboid currentCuboid : this.getCuboids())
		{
			QubbleCuboid foundParent = this.getParent(currentCuboid, cuboid);

			if (foundParent != null)
			{
				return foundParent;
			}
		}

		return null;
	}

	private QubbleCuboid getParent(QubbleCuboid parent, QubbleCuboid cuboid)
	{
		if (parent.getChildren().contains(cuboid))
		{
			return parent;
		}

		for (QubbleCuboid child : parent.getChildren())
		{
			QubbleCuboid foundParent = this.getParent(child, cuboid);

			if (foundParent != null)
			{
				return foundParent;
			}
		}

		return null;
	}

	public QubbleModel unparent()
	{
		List<QubbleCuboid> unparentedCuboids = new ArrayList<QubbleCuboid>();

		for (QubbleCuboid cuboid : this.cuboids)
		{
			List<QubbleCuboid> parentCuboids = new ArrayList<QubbleCuboid>();
			parentCuboids.add(cuboid);
			unparentedCuboids.add(cuboid);
			this.unparentCuboids(new ArrayList<QubbleCuboid>(cuboid.getChildren()), unparentedCuboids, parentCuboids);
		}

		this.cuboids.clear();
		this.cuboids.addAll(unparentedCuboids);
		return this;
	}

	private void unparentCuboids(List<QubbleCuboid> cuboids, List<QubbleCuboid> childCuboids, List<QubbleCuboid> parentCuboids)
	{
		for (QubbleCuboid cuboid : cuboids)
		{
			List<QubbleCuboid> newParentCuboids = new ArrayList<QubbleCuboid>(parentCuboids);
			newParentCuboids.add(cuboid);

			float[][] transformation = this.getParentTransformation(newParentCuboids);

			List<QubbleCuboid> children = new ArrayList<QubbleCuboid>(cuboid.getChildren());
			QubbleCuboid newCube = QubbleCuboid.create(cuboid.getName());
			newCube.setName(cuboid.getName());
			newCube.setDimensions(cuboid.getDimensionX(), cuboid.getDimensionY(), cuboid.getDimensionZ());
			newCube.setPosition(transformation[0][0], transformation[0][1], transformation[0][2]);
			newCube.setOffset(cuboid.getOffsetX(), cuboid.getOffsetY(), cuboid.getOffsetZ());
			newCube.setRotation(transformation[1][0], transformation[1][1], transformation[1][2]);
			newCube.setScale(cuboid.getScaleX(), cuboid.getScaleY(), cuboid.getScaleZ());
			newCube.setTexture(cuboid.getTextureX(), cuboid.getTextureY());
			newCube.setTextureMirrored(cuboid.isTextureMirrored());
			childCuboids.add(newCube);

			this.unparentCuboids(children, childCuboids, new ArrayList<QubbleCuboid>(newParentCuboids));
		}
	}

	private float[][] getParentTransformation(List<QubbleCuboid> parentCuboids)
	{
		Matrix4d matrix = new Matrix4d();
		matrix.setIdentity();
		Matrix4d transform = new Matrix4d();

		for (QubbleCuboid cuboid : parentCuboids)
		{
			transform.setIdentity();
			transform.setTranslation(new Vector3d(cuboid.getPositionX(), cuboid.getPositionY(), cuboid.getPositionZ()));
			matrix.mul(transform);
			transform.rotZ(cuboid.getRotationZ() / 180 * Math.PI);
			matrix.mul(transform);
			transform.rotY(cuboid.getRotationY() / 180 * Math.PI);
			matrix.mul(transform);
			transform.rotX(cuboid.getRotationX() / 180 * Math.PI);
			matrix.mul(transform);
		}

		double sinRotationAngleY, cosRotationAngleY, sinRotationAngleX, cosRotationAngleX, sinRotationAngleZ, cosRotationAngleZ;
		sinRotationAngleY = -matrix.m20;
		cosRotationAngleY = Math.sqrt(1 - sinRotationAngleY * sinRotationAngleY);

		if (Math.abs(cosRotationAngleY) > 0.0001)
		{
			sinRotationAngleX = matrix.m21 / cosRotationAngleY;
			cosRotationAngleX = matrix.m22 / cosRotationAngleY;
			sinRotationAngleZ = matrix.m10 / cosRotationAngleY;
			cosRotationAngleZ = matrix.m00 / cosRotationAngleY;
		}
		else
		{
			sinRotationAngleX = -matrix.m12;
			cosRotationAngleX = matrix.m11;
			sinRotationAngleZ = 0;
			cosRotationAngleZ = 1;
		}

		float rotationAngleX = (float) (this.epsilon((float) Math.atan2(sinRotationAngleX, cosRotationAngleX)) / Math.PI * 180);
		float rotationAngleY = (float) (this.epsilon((float) Math.atan2(sinRotationAngleY, cosRotationAngleY)) / Math.PI * 180);
		float rotationAngleZ = (float) (this.epsilon((float) Math.atan2(sinRotationAngleZ, cosRotationAngleZ)) / Math.PI * 180);
		return new float[][] { { this.epsilon((float) matrix.m03), this.epsilon((float) matrix.m13), this.epsilon((float) matrix.m23) }, { rotationAngleX, rotationAngleY, rotationAngleZ } };
	}

	private float epsilon(float x)
	{
		return x < 0 ? x > -0.0001F ? 0 : x : x < 0.0001F ? 0 : x;
	}

	public QubbleModel copy()
	{
		QubbleModel model = QubbleModel.create(this.getName(), this.getAuthor(), this.getTextureWidth(), this.getTextureHeight());

		for (QubbleCuboid cuboid : this.getCuboids())
		{
			model.getCuboids().add(cuboid.copy());
		}

		model.setFileName(this.getFileName());
		model.getTextures().putAll(this.getTextures());
		return model;
	}

	public List<QubbleCuboid> getAllCuboids()
	{
		List<QubbleCuboid> cuboids = new ArrayList<>();

		for (QubbleCuboid cuboid : this.cuboids)
		{
			cuboids.add(cuboid);
			cuboids.addAll(cuboid.getAllChildren());
		}

		return cuboids;
	}
}
