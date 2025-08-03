package com.qichen.bettersleep.network;

import com.mojang.logging.LogUtils;
import com.qichen.bettersleep.data.FatigueData;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class FatigueClientPayloadHandler {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static void handleData(FatiguePayload fatiguePayload, IPayloadContext iPayloadContext) {
        float fatigue = fatiguePayload.getFatigue();
        LOGGER.info("收到了"+ fatigue);
        Player player = iPayloadContext.player();
        player.setData(FatigueData.FATIGUE.get(),fatigue);
    }
}
