package com.qichen.bettersleep.data;

/**
 * 疲劳值配置类
 * 集中管理所有疲劳值相关的配置参数
 */
public class FatigueConfig {
    
    // 疲劳值范围
    public static final float MAX_FATIGUE = 100.0f;
    public static final float MIN_FATIGUE = -100.0f;
    
    // 基础疲劳值变化
    public static final float BASE_FATIGUE_INCREASE = 0.02f; // 每tick基础疲劳值增加
    
    // 活动相关疲劳值变化
    public static final float SPRINT_FATIGUE_INCREASE = 0.05f; // 疾跑时额外疲劳值增加
    public static final float JUMP_FATIGUE_INCREASE = 0.01f; // 跳跃时疲劳值增加
    public static final float SWIM_FATIGUE_INCREASE = 0.03f; // 游泳时疲劳值增加
    public static final float MINING_FATIGUE_INCREASE = 0.01f; // 挖掘时疲劳值增加
    
    // 事件相关疲劳值变化
    public static final float SLEEP_FATIGUE_DECREASE = -20.0f; // 睡觉时疲劳值减少
    public static final float HURT_FATIGUE_INCREASE = 0.5f; // 受伤时疲劳值增加
    public static final float DEATH_FATIGUE_INCREASE = 5.0f; // 死亡时疲劳值增加
    
    // 时间相关调整
    public static final float NIGHT_TIME_MULTIPLIER = 1.5f; // 夜晚疲劳值增加倍数
    
    // 时间范围（游戏时间）
    public static final long NIGHT_START_TIME = 13000; // 夜晚开始时间
    public static final long NIGHT_END_TIME = 23000; // 夜晚结束时间
    
    // 跳跃检测
    public static final double JUMP_VELOCITY_THRESHOLD = 0.1; // 跳跃速度阈值
    
    // 疲劳值历史记录配置
    public static final int MAX_HISTORY_SIZE = 1000; // 最大历史记录数量
    
    /**
     * 获取疲劳值变化配置
     */
    public static class FatigueChangeConfig {
        private final float baseIncrease;
        private final float sprintIncrease;
        private final float jumpIncrease;
        private final float swimIncrease;
        private final float miningIncrease;
        private final float nightMultiplier;
        
        public FatigueChangeConfig() {
            this.baseIncrease = BASE_FATIGUE_INCREASE;
            this.sprintIncrease = SPRINT_FATIGUE_INCREASE;
            this.jumpIncrease = JUMP_FATIGUE_INCREASE;
            this.swimIncrease = SWIM_FATIGUE_INCREASE;
            this.miningIncrease = MINING_FATIGUE_INCREASE;
            this.nightMultiplier = NIGHT_TIME_MULTIPLIER;
        }
        
        public FatigueChangeConfig(float baseIncrease, float sprintIncrease, float jumpIncrease, 
                                 float swimIncrease, float miningIncrease, float nightMultiplier) {
            this.baseIncrease = baseIncrease;
            this.sprintIncrease = sprintIncrease;
            this.jumpIncrease = jumpIncrease;
            this.swimIncrease = swimIncrease;
            this.miningIncrease = miningIncrease;
            this.nightMultiplier = nightMultiplier;
        }
        
        // Getters
        public float getBaseIncrease() { return baseIncrease; }
        public float getSprintIncrease() { return sprintIncrease; }
        public float getJumpIncrease() { return jumpIncrease; }
        public float getSwimIncrease() { return swimIncrease; }
        public float getMiningIncrease() { return miningIncrease; }
        public float getNightMultiplier() { return nightMultiplier; }
    }
    
    /**
     * 获取事件疲劳值变化配置
     */
    public static class EventFatigueConfig {
        private final float sleepDecrease;
        private final float hurtIncrease;
        private final float deathIncrease;
        
        public EventFatigueConfig() {
            this.sleepDecrease = SLEEP_FATIGUE_DECREASE;
            this.hurtIncrease = HURT_FATIGUE_INCREASE;
            this.deathIncrease = DEATH_FATIGUE_INCREASE;
        }
        
        public EventFatigueConfig(float sleepDecrease, float hurtIncrease, float deathIncrease) {
            this.sleepDecrease = sleepDecrease;
            this.hurtIncrease = hurtIncrease;
            this.deathIncrease = deathIncrease;
        }
        
        // Getters
        public float getSleepDecrease() { return sleepDecrease; }
        public float getHurtIncrease() { return hurtIncrease; }
        public float getDeathIncrease() { return deathIncrease; }
    }
    
    /**
     * 获取默认疲劳值变化配置
     */
    public static FatigueChangeConfig getDefaultFatigueChangeConfig() {
        return new FatigueChangeConfig();
    }
    
    /**
     * 获取默认事件疲劳值配置
     */
    public static EventFatigueConfig getDefaultEventFatigueConfig() {
        return new EventFatigueConfig();
    }
} 