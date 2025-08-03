package com.qichen.bettersleep.event;

import com.mojang.logging.LogUtils;
import com.qichen.bettersleep.data.FatigueData;
import com.qichen.bettersleep.network.FatiguePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import static com.qichen.bettersleep.BetterSleep.MODID;
import static com.qichen.bettersleep.data.FatigueData.FATIGUE;

@EventBusSubscriber(modid= MODID)
public class ModPlayerTickEvent {

    public static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void UpgradeFatigue(PlayerTickEvent.Post event){
        if (event.getEntity().level().isClientSide()) return; // 只在服务器端处理
        Player player= event.getEntity();
        float fatigue=player.getData(FATIGUE.get());
        float oldFatigue=fatigue;
        // 增加疲劳值
        if(player.isSprinting()||player.isSwimming()){
            fatigue+=0.1f;
        }

        //减少疲劳值
        if(player.isSleeping()){
            fatigue-=0.1f;
        }else if(!player.walkAnimation.isMoving()){
            fatigue-=0.05f;
        }
        // 限制疲劳值范围
        fatigue = Math.max(-100.0f, Math.min(100.0f, fatigue));
        player.setData(FATIGUE.get(), fatigue);
        //发包
        if(oldFatigue!= fatigue){
            PacketDistributor.sendToPlayer((ServerPlayer) player, new FatiguePayload(fatigue));
        }
    }
}
