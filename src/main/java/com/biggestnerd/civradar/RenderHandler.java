package com.biggestnerd.civradar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

import com.biggestnerd.civradar.Config.NameLocation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class RenderHandler extends Gui {

	private Config config = CivRadar.instance.getConfig();
	private Minecraft mc = Minecraft.getMinecraft();
	private Color radarColor;
	private double pingDelay = 63.0D;
	private List<?> entityList;
	private float radarScale;
	ArrayList<String> inRangePlayers;

	public RenderHandler() {
		inRangePlayers = new ArrayList<String>();
	}

	@SubscribeEvent
	public void renderRadar(RenderGameOverlayEvent event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			return;
		}
		if (config.isEnabled()) {
			drawRadar();
		}
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START && mc.theWorld != null) {
			if (pingDelay <= -10.0D) {
				pingDelay = 63.0D;
			}
			pingDelay -= 1.0D;
			entityList = mc.theWorld.loadedEntityList;
			ArrayList<String> newInRangePlayers = new ArrayList<String>();
			for (Object o : entityList) {
				if (o instanceof EntityOtherPlayerMP) {
					newInRangePlayers.add(((EntityOtherPlayerMP) o).getName());
				}
			}
			ArrayList<String> temp = (ArrayList<String>) newInRangePlayers.clone();
			newInRangePlayers.removeAll(inRangePlayers);
			//while (newInRangePlayers.iterator().hasNext()) {
			//	mc.thePlayer.playSound(new SoundEvent(new ResourceLocation("block.note.pling")), config.getPingVolume(), 1.0F);
			//}
			inRangePlayers = temp;
		}
	}

	@SubscribeEvent
	public void renderWaypoints(RenderWorldLastEvent event) {
		if (CivRadar.instance.getWaypointSave() == null) {
			return;
		}
		if (config.isRenderWaypoints()) {
			for (Waypoint point : CivRadar.instance.getWaypointSave().getWaypoints()) {
				if (point.getDimension() == mc.theWorld.provider.getDimension() && point.isEnabled()) {
					renderWaypoint(point, event);
				}
			}
		}
	}

	private void drawRadar() {

		radarColor = config.getRadarColor();
		radarScale = config.getRadarScale();

		ScaledResolution res = new ScaledResolution(mc);
		int width = res.getScaledWidth();

		GlStateManager.pushMatrix();
		GlStateManager.translate(width - (65 * radarScale) + (config.getRadarX()), (65 * radarScale) + (config.getRadarY()), 0.0F);
		GlStateManager.scale(1.0F, 1.0F, 1.0F);

		if (config.isRenderCoordinates()) {
			String coords = "(" + (int) mc.thePlayer.posX + "," + (int) mc.thePlayer.posY + "," + (int) mc.thePlayer.posZ + ")";
			mc.fontRendererObj.drawStringWithShadow(coords, -(mc.fontRendererObj.getStringWidth(coords) / 2), 65 * radarScale, 14737632);
		}

		GlStateManager.scale(radarScale, radarScale, radarScale);
		GlStateManager.rotate(-mc.thePlayer.rotationYaw, 0.0F, 0.0F, 1.0F);

		drawCircle(0, 0, 63.0D, radarColor, true);
		GlStateManager.glLineWidth(2.0f);
		drawCircle(0, 0, 63.0D, radarColor, false);
		GlStateManager.glLineWidth(1.0f);

		if (pingDelay > 0) {
			drawCircle(0, 0, 63.0D - pingDelay, radarColor, false);
		}
		GlStateManager.glLineWidth(2.0f);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GlStateManager.color(radarColor.getRed() / 255.0F, radarColor.getGreen() / 255.0F, radarColor.getBlue() / 255.0F, config.getRadarOpacity() + 0.5F);
		buffer.pos(0.0D, -63.0D, 0.0D);
		buffer.pos(0.0D, 63.0D, 0.0D);
		buffer.pos(-63.0D, 0.0D, 0.0D);
		buffer.pos(63.0D, 0.0D, 0.0D);
		buffer.pos(-44.5D, -44.5D, 0.0D);
		buffer.pos(44.5D, 44.5D, 0.0D);
		buffer.pos(-44.5D, 44.5D, 0.0D);
		buffer.pos(44.5D, -44.5D, 0.0D);
		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();

		drawRadarIcons();

		GlStateManager.rotate(mc.thePlayer.rotationYaw, 0.0F, 0.0F, 1.0F);

		drawTriangle(0, 0, Color.WHITE);
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	private void drawCircle(int x, int y, double radius, Color c, boolean filled) {
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, filled ? config.getRadarOpacity() : config.getRadarOpacity() + 0.5F);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
		for (int i = 0; i <= 360; i++) {
			double x2 = Math.sin(i * Math.PI / 180.0D) * radius;
			double y2 = Math.cos(i * Math.PI / 180.0D) * radius;
			buffer.pos(x + x2, y + y2, 0.0D).endVertex();
		}
		tessellator.draw();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	private void drawTriangle(int x, int y, Color c) {
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, config.getRadarOpacity() + 0.5F);
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
		buffer.pos(x, y + 3, 0.0D).endVertex();
		buffer.pos(x + 3, y - 3, 0.0D).endVertex();
		buffer.pos(x - 3, y - 3, 0.0D).endVertex();
		tessellator.draw();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.rotate(-180.0F, 0.0F, 0.0F, 1.0F);
	}

	private void drawRadarIcons() {
		if (entityList == null) {
			return;
		}
		for (Object o : entityList) {
			Entity e = (Entity) o;
			int playerPosX = (int) mc.thePlayer.posX;
			int playerPosZ = (int) mc.thePlayer.posZ;
			int entityPosX = (int) e.posX;
			int entityPosZ = (int) e.posZ;
			int displayPosX = playerPosX - entityPosX;
			int displayPosZ = playerPosZ - entityPosZ;
			if (e != mc.thePlayer) {
				if (e instanceof EntityItem) {
					EntityItem item = (EntityItem) e;
					if (config.isRender(EntityItem.class)) {
						renderItemIcon(displayPosX, displayPosZ, item.getEntityItem());
					}
				} else if (e instanceof EntityOtherPlayerMP) {
					if (config.isRender(EntityPlayer.class)) {
						EntityOtherPlayerMP eop = (EntityOtherPlayerMP) e;
						try {
							renderPlayerHeadIcon(displayPosX, displayPosZ, eop);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} else if (e instanceof EntityMinecart) {
					if (config.isRender(EntityMinecart.class)) {
						ItemStack cart = new ItemStack(Items.minecart);
						renderItemIcon(displayPosX, displayPosZ, cart);
					}
				} else if (config.isRender(o.getClass())) {
					renderIcon(displayPosX, displayPosZ, config.getMob(o.getClass()).getResource());
				}
			}
		}
	}

	private void renderItemIcon(int x, int y, ItemStack item) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		GlStateManager.translate(x + 1, y + 1, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, config.getIconOpacity());
		GlStateManager.rotate(mc.thePlayer.rotationYaw, 0.0F, 0.0F, 1.0F);
		mc.getRenderItem().renderItemIntoGUI(item, -8, -8);
		GlStateManager.translate(-x - 1, -y - 1, 0.0F);
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}

	private void renderPlayerHeadIcon(int x, int y, EntityOtherPlayerMP player) throws Exception {
		GlStateManager.color(1.0F, 1.0F, 1.0F, config.getIconOpacity());
		GlStateManager.enableBlend();
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		GlStateManager.translate(x + 1, y + 1, 0.0F);
		GlStateManager.rotate(mc.thePlayer.rotationYaw, 0.0F, 0.0F, 1.0F);
		mc.getTextureManager().bindTexture(new ResourceLocation("civRadar/icons/player.png"));
		drawModalRectWithCustomSizedTexture(-8, -8, 0, 0, 16, 16, 16, 16);
		GlStateManager.translate(-x - 1, -y - 1, 0.0F);
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		
		if (config.isPlayerNames()) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(x - 8, y, 0.0F);
			GlStateManager.rotate(mc.thePlayer.rotationYaw, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-x, -y, 0.0F);
			String playerName = player.getName();
			if (config.isExtraPlayerInfo()) {
				playerName += " (" + (int) mc.thePlayer.getDistanceToEntity(player) + "m)(Y" + (int) player.posY + ")";
			}
			int yOffset = config.getNameLocation() == NameLocation.below ? 10 : -10;
			drawCenteredString(mc.fontRendererObj, playerName, x + 8, y + yOffset, Color.WHITE.getRGB());
			GlStateManager.scale(2.0F, 2.0F, 2.0F);
			GlStateManager.popMatrix();
		}
	}

	private void renderIcon(int x, int y, ResourceLocation resource) {
		mc.getTextureManager().bindTexture(resource);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, config.getIconOpacity());
		GL11.glEnable(3042);
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glTranslatef(x + 1, y + 1, 0.0F);
		GL11.glRotatef(mc.thePlayer.rotationYaw, 0.0F, 0.0F, 1.0F);
		drawModalRectWithCustomSizedTexture(-8, -8, 0, 0, 16, 16, 16, 16);
		GL11.glTranslatef(-x - 1, -y - 1, 0.0F);
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		GL11.glDisable(2896);
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}

	private void renderWaypoint(Waypoint point, RenderWorldLastEvent event) {
		String name = point.getName();
		Color c = point.getColor();
		float partialTickTime = event.getPartialTicks();
		double distance = point.getDistance(mc);
		int maxView = mc.gameSettings.renderDistanceChunks * 22;
		if (distance <= config.getMaxWaypointDistance() || config.getMaxWaypointDistance() < 0) {
			FontRenderer fr = mc.fontRendererObj;
			Tessellator tess = Tessellator.getInstance();
			VertexBuffer vb = tess.getBuffer();
			RenderManager rm = mc.getRenderManager();

			float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
			float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
			float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

			float displayX = (float) point.getX() - playerX;
			float displayY = (float) point.getY() + 1.3f - playerY;
			float displayZ = (float) point.getZ() - playerZ;

			// z is what would typically be y here btw
			// The ever so lovely Laura [REDACTED] is a math godess
			// While she did not provide the math used here, she is still very
			// good at math
			// Also she was very helpful in me figuring this math out for myself
			// I hope this makes her happy
			if (distance > maxView) {
				float slope = displayZ / displayX;
				displayX = Math.abs((float) Math.sqrt(Math.pow(maxView, 2) / (1 + Math.pow(slope, 2)))) * (point.getX() < 0 ? -1 : 1);
				displayZ = slope * displayX;
			}

			float scale = 0.45F * (float) (Math.max(10.0D, Math.min(distance, maxView)) / 120);// old scaling math: (float) (Math.max(2, distance /5) * 0.0185f);

			GL11.glColor4f(1f, 1f, 1f, 1f);
			GL11.glPushMatrix();
			GL11.glTranslatef(displayX, displayY, displayZ);
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-scale, -scale, scale);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			name += " (" + (int) distance + "m)";
			int width = fr.getStringWidth(name);
			int height = 10;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			int stringMiddle = width / 2;
			vb.color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, config.getWaypointOpcaity());
			vb.pos(-stringMiddle - 1, -1, 0.0D);
			vb.pos(-stringMiddle - 1, 1 + height, 0.0D);
			vb.pos(stringMiddle + 1, 1 + height, 0.0D);
			vb.pos(stringMiddle + 1, -1, 0.0D);
			tess.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			fr.drawString(name, -width / 2, 1, Color.WHITE.getRGB());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
	}
}
