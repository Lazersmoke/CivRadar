package com.biggestnerd.civradar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RadarHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RadarEntity {

	private static final Logger logger = LogManager.getLogger();
	private static final String[] HORSE_VARIANTS = {"white", "creamy", "chestnut", "brown", "black", "gray", "darkbrown"};

	private final String className;
	private boolean enabled = true;

	public RadarEntity(Class entityClass) {
		className = entityClass.getName();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Class<? extends Entity> getEntityClass() {
		try {
			return (Class<? extends Entity>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Entity.class;
	}

	public String getName() {
		return className;
	}

	public String getEntityName() {
		String[] className = this.className.split("\\.");
		return className[className.length - 1].substring(6);
	}

	public static ResourceLocation getResourceJM(Entity entity) {
		try {
			FMLClientHandler instance = FMLClientHandler.instance();
			Minecraft client = instance.getClient();
			RenderManager renderManager = client.getRenderManager();
			Render render = renderManager.getEntityRenderObject(entity);
			ResourceLocation original;
			if (render instanceof RenderHorse) {
				EntityHorse horseEntity = (EntityHorse) entity;
				int horseVariant = 0xff & horseEntity.getHorseVariant();
				if(horseEntity.getType() == HorseType.HORSE) {
					if (horseVariant < HORSE_VARIANTS.length) {
						original = new ResourceLocation("minecraft", "textures/entity/horse/horse_" + HORSE_VARIANTS[horseVariant] + ".png");
					} else {
						original = null;
					}
				} else {
					original = HorseType.getArmorType(horseVariant).getTexture();
				}
			} else {
				original = RadarHelper.getEntityTexture(render, entity);
			}

			if (original == null) {
				logger.error("Can't get entityTexture for " + entity.getClass() + " via " + render, (Throwable)null);
				return null;
			}

			return new ResourceLocation(original.getResourceDomain(), original.getResourcePath().replace("/entity/", "/entity_icon_journeymap/"));

		} catch (Throwable e) {
			logger.error("Can't get entityTexture for " + entity.getName(), e);
			return null;
		}
	}
}
