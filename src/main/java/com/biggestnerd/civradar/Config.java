package com.biggestnerd.civradar;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config {
	
	private boolean enabled = true;
	private ArrayList<RadarEntity> mobs;
	private boolean renderCoordinates = true;
	private boolean extraPlayerInfo = true;
	private boolean playerNames = true;
	private int radarX = 0;
	private int radarY = 0;
	private int maxWaypointDistance = 500;
	private float radarOpacity = 0.5F;
	private float iconOpacity = 1.0F;
	private float waypointOpcaity = 0.5F;
	private boolean renderWaypoints = true;
	private Color radarColor = new Color(0.0F, 0.5F, 0.5F);
	private float radarScale = 1.0F;
	public enum NameLocation {above,below};
	private NameLocation nameLocation = NameLocation.below;
	private float pingVolume = 0.0F;
	
	public Config() {
		mobs = new ArrayList<RadarEntity>(Arrays.asList(new RadarEntity[]{
				new RadarEntity(EntityBat.class),
				new RadarEntity(EntityChicken.class),
				new RadarEntity(EntityCow.class),
				new RadarEntity(EntityHorse.class),
				new RadarEntity(EntityMooshroom.class),
				new RadarEntity(EntityOcelot.class),
				new RadarEntity(EntityPig.class),
				new RadarEntity(EntityRabbit.class),
				new RadarEntity(EntitySheep.class),
				new RadarEntity(EntitySquid.class),
				new RadarEntity(EntityVillager.class),
				new RadarEntity(EntityWolf.class),
				new RadarEntity(EntityBlaze.class),
				new RadarEntity(EntityCaveSpider.class),
				new RadarEntity(EntityCreeper.class),
				new RadarEntity(EntityEnderman.class),
				new RadarEntity(EntityGhast.class),
				new RadarEntity(EntityGolem.class),
				new RadarEntity(EntityGuardian.class),
				new RadarEntity(EntityIronGolem.class),
				new RadarEntity(EntityMagmaCube.class),
				new RadarEntity(EntityPigZombie.class),
				new RadarEntity(EntitySilverfish.class),
				new RadarEntity(EntitySkeleton.class),
				new RadarEntity(EntitySlime.class),
				new RadarEntity(EntitySnowman.class),
				new RadarEntity(EntitySpider.class),
				new RadarEntity(EntityWitch.class),
				new RadarEntity(EntityZombie.class),
				new RadarEntity(EntityItem.class),
				new RadarEntity(EntityBoat.class),
				new RadarEntity(EntityMinecart.class),
				new RadarEntity(EntityPlayer.class)
				}));
	}

	public ArrayList<RadarEntity> getEntities() {
		ArrayList<RadarEntity> allEntities = new ArrayList<RadarEntity>();
		allEntities.addAll(mobs);
		return allEntities;
	}
	
	public void setRender(Class entityClass, boolean enabled) {
		for(RadarEntity e : mobs) {
			if(e.getEntityClass().equals(entityClass))
				e.setEnabled(enabled);
		}
	}
	
	public boolean isRender(Class entityClass) {
		for(RadarEntity e : mobs) {
			if(e.getEntityClass().equals(entityClass)) {
				return e.isEnabled();
			}
		}
		return false;
	}
	
	public NameLocation getNameLocation() {
		return nameLocation;
	}
	
	public void switchNameLocation() {
		if(nameLocation == NameLocation.above) {
			nameLocation = NameLocation.below;
		} else {
			nameLocation = NameLocation.above;
		}
	}
	
	public void setRadarScale(float radarScale) {
		this.radarScale = radarScale;
	}
	
	public float getRadarScale() {
		return radarScale;
	}
	
	public RadarEntity getMob(Class entityClass) {
		for(RadarEntity e : mobs) {
			if(e.getEntityClass().equals(entityClass))
				return e;
		}
		return null;
	}
	
	public void setExtraPlayerInfo(boolean extraPlayerInfo) {
		this.extraPlayerInfo = extraPlayerInfo;
	}
	
	public boolean isExtraPlayerInfo() {
		return extraPlayerInfo;
	}
	
	public float getPingVolume() {
		return pingVolume;
	}
	
	public void setPingVolume(float pingVolume) {
		this.pingVolume = pingVolume;
	}
	
	public boolean isPlayerNames() {
		return playerNames;
	}

	public void setPlayerNames(boolean playerNames) {
		this.playerNames = playerNames;
	}

	public void setColor(Color c) {
		this.radarColor = c;
	}
	
	public void setColor(float red, float green, float blue) {
		this.radarColor = new Color(red, green, blue);
	}
	
	public Color getRadarColor() {
		return radarColor;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isRenderCoordinates() {
		return renderCoordinates;
	}
	public void setRenderCoordinates(boolean renderCoordinates) {
		this.renderCoordinates = renderCoordinates;
	}
	
	public int getRadarX() {
		return radarX;
	}
	public void setRadarX(int radarX) {
		this.radarX = radarX;
	}
	public int getRadarY() {
		return radarY;
	}
	public void setRadarY(int radarY) {
		this.radarY = radarY;
	}
	
	public int getMaxWaypointDistance() {
		return maxWaypointDistance;
	}
	public void setMaxWaypointDistance(int maxWaypointDistance) {
		this.maxWaypointDistance = maxWaypointDistance;
	}
	public float getRadarOpacity() {
		return radarOpacity;
	}

	public void setRadarOpacity(float radarOpacity) {
		this.radarOpacity = radarOpacity;
	}
	
	public float getIconOpacity() {
		return iconOpacity;
	}
	
	public void setIconOpacity(float iconOpacity) {
		this.iconOpacity = iconOpacity;
	}

	public float getWaypointOpcaity() {
		return waypointOpcaity;
	}

	public void setWaypointOpcaity(float waypointOpcaity) {
		this.waypointOpcaity = waypointOpcaity;
	}
	
	public boolean isRenderWaypoints() {
		return renderWaypoints;
	}
	
	public void setRenderWaypoints(boolean renderWaypoints) {
		this.renderWaypoints = renderWaypoints;
	}

	public void save(File file) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			String json = gson.toJson(this);
			
			FileWriter fw = new FileWriter(file);
			fw.write(json);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Config load(File file) {
		Gson gson = new Gson();
		try {
			return (Config) gson.fromJson(new FileReader(file), Config.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Config();
	}
}
