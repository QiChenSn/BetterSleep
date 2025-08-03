package com.qichen.bettersleep.gui;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.slf4j.Logger;


import static com.qichen.bettersleep.BetterSleep.MODID;
import static com.qichen.bettersleep.data.FatigueData.FATIGUE;


@EventBusSubscriber(modid= MODID)
public class GuiRander {
    public static final Logger LOGGER = LogUtils.getLogger();
    // 渲染疲劳值 HUD
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.screen != null) return; // 仅在游戏中渲染

        float fatigue = player.getData(FATIGUE.get());
        GuiGraphics guiGraphics = event.getGuiGraphics();

        // 获取屏幕尺寸
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        // 进度条位置（屏幕底部中心）
        int barWidth = 100;
        int barHeight = 10;
        int x = (screenWidth - barWidth) / 2;
        int y = screenHeight - 40; // 在饥饿条上方

        // 绘制背景
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF555555); // 灰色背景

        // 绘制进度条（根据疲劳值）
        int filledWidth = (int) (barWidth * (fatigue / 100.0f));
        guiGraphics.fill(x, y, x + filledWidth, y + barHeight, 0xFF00FF00); // 绿色填充

        // 绘制疲劳值文本
        String fatigueText = String.format("疲劳值: %.1f", fatigue);
        int textX = x + (barWidth - minecraft.font.width(fatigueText)) / 2;
        int textY = y - 10;
        guiGraphics.drawString(minecraft.font, fatigueText, textX, textY, 0xFFFFFF, true);
    }
}
