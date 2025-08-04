package com.qichen.bettersleep.data;

import com.mojang.logging.LogUtils;
import com.qichen.bettersleep.network.FatiguePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.qichen.bettersleep.data.FatigueData.FATIGUE;

/**
 * 疲劳值管理器
 * 负责处理所有与疲劳值相关的逻辑
 */
public class FatigueManager {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // 使用配置类
    private static final FatigueConfig.FatigueChangeConfig fatigueChangeConfig = 
        FatigueConfig.getDefaultFatigueChangeConfig();
    private static final FatigueConfig.EventFatigueConfig eventFatigueConfig = 
        FatigueConfig.getDefaultEventFatigueConfig();
    
    // 玩家疲劳值历史记录（用于调试和统计）
    private static final Map<UUID, FatigueHistory> playerFatigueHistory = new HashMap<>();
    
    /**
     * 处理玩家tick事件
     */
    public static void handlePlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        
        float currentFatigue = player.getData(FATIGUE.get());
        float newFatigue = calculateFatigueChange(player, currentFatigue);
        
        // 应用疲劳值变化
        applyFatigueChange(player, newFatigue);
    }
    
    /**
     * 处理玩家死亡事件
     */
    public static void handlePlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        
        float currentFatigue = player.getData(FATIGUE.get());
        float newFatigue = currentFatigue + eventFatigueConfig.getDeathIncrease();
        
        applyFatigueChange(player, newFatigue);
        LOGGER.info("玩家 {} 死亡重生，疲劳值从 {} 增加到 {}", 
                   player.getName().getString(), currentFatigue, newFatigue);
    }
    
    /**
     * 计算疲劳值变化
     */
    private static float calculateFatigueChange(Player player, float currentFatigue) {
        float fatigueChange=0.0f;
        if(player.walkAnimation.isMoving()){
            LOGGER.info("FUCK");
            fatigueChange += fatigueChangeConfig.getBaseIncrease();
        }
        // 根据玩家活动调整疲劳值
        if (player.isSprinting()) {
            fatigueChange += fatigueChangeConfig.getSprintIncrease();
        }
        
        if (player.isInWater()) {
            fatigueChange += fatigueChangeConfig.getSwimIncrease();
        }
        
        // 检查是否在挖掘
        if (isPlayerMining(player)) {
            fatigueChange += fatigueChangeConfig.getMiningIncrease();
        }
        
        // 检查是否刚跳跃
        if (hasPlayerJustJumped(player)) {
            fatigueChange += fatigueChangeConfig.getJumpIncrease();
        }
        
        // 根据时间调整疲劳值（夜晚疲劳值增加更快）
        if (isNightTime(player.level())) {
            fatigueChange *= fatigueChangeConfig.getNightMultiplier();
        }
        
        return currentFatigue + fatigueChange;
    }
    
    /**
     * 应用疲劳值变化
     */
    private static void applyFatigueChange(Player player, float newFatigue) {
        float oldFatigue = player.getData(FATIGUE.get());
        
        // 限制疲劳值范围
        newFatigue = Math.max(FatigueConfig.MIN_FATIGUE, Math.min(FatigueConfig.MAX_FATIGUE, newFatigue));
        
        // 更新疲劳值
        player.setData(FATIGUE.get(), newFatigue);
        
        // 记录疲劳值历史
        recordFatigueHistory(player, oldFatigue, newFatigue);
        
        // 如果疲劳值发生变化，发送网络包

        if (oldFatigue != newFatigue && player instanceof ServerPlayer serverPlayer) {
            sendFatigueUpdate(serverPlayer, newFatigue);
        }
    }
    
    /**
     * 检查玩家是否在挖掘
     */
    private static boolean isPlayerMining(Player player) {
        return player.isUsingItem() && 
               player.getUseItem().getItem().toString().contains("pickaxe");
    }
    
    /**
     * 检查玩家是否刚跳跃
     */
    private static boolean hasPlayerJustJumped(Player player) {
        // 这里可以通过检查玩家的垂直速度来判断是否刚跳跃
        return player.getDeltaMovement().y > FatigueConfig.JUMP_VELOCITY_THRESHOLD;
    }
    
    /**
     * 检查是否为夜晚
     */
    private static boolean isNightTime(Level level) {
        long time = level.getDayTime();
        return time >= FatigueConfig.NIGHT_START_TIME && time <= FatigueConfig.NIGHT_END_TIME;
    }
    
    /**
     * 记录疲劳值历史
     */
    private static void recordFatigueHistory(Player player, float oldFatigue, float newFatigue) {
        UUID playerId = player.getUUID();
        FatigueHistory history = playerFatigueHistory.computeIfAbsent(playerId, 
                                                                      k -> new FatigueHistory());
        history.recordChange(oldFatigue, newFatigue);
    }
    
    /**
     * 发送疲劳值更新包
     */
    private static void sendFatigueUpdate(ServerPlayer player, float fatigue) {
        PacketDistributor.sendToPlayer(player, new FatiguePayload(fatigue));
    }
    
    /**
     * 获取玩家疲劳值
     */
    public static float getPlayerFatigue(Player player) {
        return player.getData(FATIGUE.get());
    }
    
    /**
     * 设置玩家疲劳值
     */
    public static void setPlayerFatigue(Player player, float fatigue) {
        Float oldFatigue = player.getData(FATIGUE.get());
        float clampedFatigue = Math.max(FatigueConfig.MIN_FATIGUE, Math.min(FatigueConfig.MAX_FATIGUE, fatigue));
        player.setData(FATIGUE.get(), clampedFatigue);
        if(fatigue!=oldFatigue){sendFatigueUpdate((ServerPlayer) player, fatigue);}
    }
    
    /**
     * 增加玩家疲劳值
     */
    public static void addPlayerFatigue(Player player, float amount) {
        float currentFatigue = getPlayerFatigue(player);
        setPlayerFatigue(player, currentFatigue + amount);
    }
    
    /**
     * 减少玩家疲劳值
     */
    public static void reducePlayerFatigue(Player player, float amount) {
        addPlayerFatigue(player, -amount);
    }
    
    /**
     * 处理玩家睡觉（手动调用）
     */
    public static void handlePlayerSleep(Player player) {
        if (player.level().isClientSide()) return;
        
        float currentFatigue = player.getData(FATIGUE.get());
        float newFatigue = currentFatigue + eventFatigueConfig.getSleepDecrease();
        
        applyFatigueChange(player, newFatigue);
        LOGGER.info("玩家 {} 睡觉，疲劳值从 {} 减少到 {}", 
                   player.getName().getString(), currentFatigue, newFatigue);
    }
    
    /**
     * 处理玩家受伤（手动调用）
     */
    public static void handlePlayerHurt(Player player) {
        if (player.level().isClientSide()) return;
        
        float currentFatigue = player.getData(FATIGUE.get());
        float newFatigue = currentFatigue + eventFatigueConfig.getHurtIncrease();
        
        applyFatigueChange(player, newFatigue);
        LOGGER.info("玩家 {} 受伤，疲劳值从 {} 增加到 {}", 
                   player.getName().getString(), currentFatigue, newFatigue);
    }
    
    /**
     * 获取玩家疲劳值历史
     */
    public static FatigueHistory getPlayerFatigueHistory(UUID playerId) {
        return playerFatigueHistory.get(playerId);
    }
    
    /**
     * 清除玩家疲劳值历史
     */
    public static void clearPlayerFatigueHistory(UUID playerId) {
        playerFatigueHistory.remove(playerId);
    }
    
    /**
     * 获取当前疲劳值变化配置
     */
    public static FatigueConfig.FatigueChangeConfig getFatigueChangeConfig() {
        return fatigueChangeConfig;
    }
    
    /**
     * 获取当前事件疲劳值配置
     */
    public static FatigueConfig.EventFatigueConfig getEventFatigueConfig() {
        return eventFatigueConfig;
    }
    
    /**
     * 疲劳值历史记录类
     */
    public static class FatigueHistory {
        private float totalChange = 0.0f;
        private int changeCount = 0;
        private float maxFatigue = 0.0f;
        private float minFatigue = 0.0f;
        
        public void recordChange(float oldFatigue, float newFatigue) {
            totalChange += (newFatigue - oldFatigue);
            changeCount++;
            maxFatigue = Math.max(maxFatigue, newFatigue);
            minFatigue = Math.min(minFatigue, newFatigue);
        }
        
        public float getAverageChange() {
            return changeCount > 0 ? totalChange / changeCount : 0.0f;
        }
        
        public int getChangeCount() {
            return changeCount;
        }
        
        public float getMaxFatigue() {
            return maxFatigue;
        }
        
        public float getMinFatigue() {
            return minFatigue;
        }
    }
}
