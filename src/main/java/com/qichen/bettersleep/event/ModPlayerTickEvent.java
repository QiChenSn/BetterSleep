package com.qichen.bettersleep.event;

import com.mojang.logging.LogUtils;
import com.qichen.bettersleep.data.FatigueManager;
import com.qichen.bettersleep.network.FatiguePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import static com.qichen.bettersleep.BetterSleep.MODID;

@EventBusSubscriber(modid= MODID)
public class ModPlayerTickEvent {

    public static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event){
        if (event.getEntity().level().isClientSide()) return; // 只在服务器端处理
        
        // 使用FatigueManager处理疲劳值逻辑
        if(event.getEntity().walkAnimation.isMoving())LOGGER.info("fuckkk");
        FatigueManager.handlePlayerTick(event);
    }
}
