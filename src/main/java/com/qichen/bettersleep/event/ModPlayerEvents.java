package com.qichen.bettersleep.event;

import com.qichen.bettersleep.data.FatigueManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static com.qichen.bettersleep.BetterSleep.MODID;

/**
 * 处理玩家相关事件的类
 */
@EventBusSubscriber(modid = MODID)
public class ModPlayerEvents {

    /**
     * 处理玩家死亡重生事件
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        
        // 使用FatigueManager处理死亡疲劳值增加
        FatigueManager.handlePlayerDeath(event);
    }
} 