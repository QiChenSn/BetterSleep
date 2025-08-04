# 疲劳值管理系统设计文档

## 概述

这个疲劳值管理系统采用了优雅的分层架构设计，将疲劳值管理逻辑从事件处理中分离出来，实现了高内聚、低耦合的设计目标。

## 系统架构

### 1. 核心组件

#### FatigueManager (疲劳值管理器)
- **位置**: `src/main/java/com/qichen/bettersleep/data/FatigueManager.java`
- **职责**: 核心疲劳值计算和管理逻辑
- **特点**: 
  - 集中处理所有疲劳值相关计算
  - 提供统一的API接口
  - 支持疲劳值历史记录
  - 自动处理网络同步

#### FatigueConfig (配置管理)
- **位置**: `src/main/java/com/qichen/bettersleep/data/FatigueConfig.java`
- **职责**: 集中管理所有疲劳值相关配置
- **特点**:
  - 可配置的疲劳值变化参数
  - 支持运行时配置调整
  - 分类管理不同类型的配置

#### FatigueData (数据存储)
- **位置**: `src/main/java/com/qichen/bettersleep/data/FatigueData.java`
- **职责**: 疲劳值数据的存储和序列化
- **特点**: 使用NeoForge的Attachment系统

### 2. 事件处理层

#### ModPlayerTickEvent (玩家tick事件)
- **位置**: `src/main/java/com/qichen/bettersleep/event/ModPlayerTickEvent.java`
- **职责**: 处理玩家tick事件，调用FatigueManager

#### ModPlayerEvents (其他玩家事件)
- **位置**: `src/main/java/com/qichen/bettersleep/event/ModPlayerEvents.java`
- **职责**: 处理睡觉、受伤、死亡等事件

## 设计优势

### 1. 分离关注点
- **事件处理**: 只负责事件监听和调用管理器
- **业务逻辑**: 集中在FatigueManager中
- **配置管理**: 独立的配置类

### 2. 可扩展性
- 新增疲劳值影响因素只需修改FatigueManager
- 配置参数可以轻松调整
- 支持添加新的疲劳值计算规则

### 3. 可维护性
- 代码结构清晰，职责明确
- 疲劳值逻辑集中管理
- 支持调试和统计功能

### 4. 可测试性
- 业务逻辑与事件处理分离
- 可以独立测试疲劳值计算逻辑
- 支持单元测试

## 疲劳值计算规则

### 基础疲劳值变化
- 每tick基础增加: `0.1`
- 疾跑额外增加: `0.2`
- 游泳额外增加: `0.3`
- 挖掘额外增加: `0.1`
- 跳跃额外增加: `0.5`

### 事件相关变化
- 睡觉减少: `-20.0`
- 受伤增加: `5.0`
- 死亡增加: `15.0`

### 时间调整
- 夜晚疲劳值增加倍数: `1.5`
- 夜晚时间范围: `13000-23000` (游戏时间)

## 使用方法

### 1. 基本API调用

```java
// 获取玩家疲劳值
float fatigue = FatigueManager.getPlayerFatigue(player);

// 设置玩家疲劳值
FatigueManager.setPlayerFatigue(player, 50.0f);

// 增加疲劳值
FatigueManager.addPlayerFatigue(player, 10.0f);

// 减少疲劳值
FatigueManager.reducePlayerFatigue(player, 10.0f);
```

### 2. 手动触发事件

```java
// 手动处理睡觉
FatigueManager.handlePlayerSleep(player);

// 手动处理受伤
FatigueManager.handlePlayerHurt(player);
```

### 3. 获取统计信息

```java
// 获取玩家疲劳值历史
FatigueManager.FatigueHistory history = 
    FatigueManager.getPlayerFatigueHistory(player.getUUID());

// 获取平均变化
float avgChange = history.getAverageChange();

// 获取变化次数
int changeCount = history.getChangeCount();
```

### 4. 配置调整

```java
// 获取当前配置
FatigueConfig.FatigueChangeConfig changeConfig = 
    FatigueManager.getFatigueChangeConfig();

FatigueConfig.EventFatigueConfig eventConfig = 
    FatigueManager.getEventFatigueConfig();
```

## 扩展指南

### 1. 添加新的疲劳值影响因素

1. 在`FatigueConfig`中添加新的配置参数
2. 在`FatigueManager.calculateFatigueChange()`中添加新的计算逻辑
3. 在`FatigueManager`中添加检测新活动的辅助方法

### 2. 添加新的事件处理

1. 在`ModPlayerEvents`中添加新的事件监听方法
2. 在`FatigueManager`中添加对应的事件处理方法
3. 在`FatigueConfig`中添加相关配置参数

### 3. 自定义疲劳值计算规则

1. 继承`FatigueConfig.FatigueChangeConfig`类
2. 重写相关计算方法
3. 在`FatigueManager`中使用自定义配置

## 网络同步

系统自动处理疲劳值的网络同步：
- 当疲劳值发生变化时，自动发送网络包
- 支持客户端和服务器端的数据同步
- 使用NeoForge的网络系统

## 调试和监控

### 1. 日志记录
- 所有疲劳值变化都会记录到日志
- 包含玩家名称、变化前后的值
- 便于调试和监控

### 2. 历史记录
- 每个玩家的疲劳值变化历史
- 支持统计分析
- 可用于性能优化

### 3. 配置验证
- 疲劳值范围自动限制
- 配置参数验证
- 防止异常值

## 性能考虑

1. **缓存配置**: 配置对象在启动时创建，避免重复创建
2. **历史记录限制**: 限制历史记录数量，防止内存泄漏
3. **事件过滤**: 只在服务器端处理，避免客户端重复计算
4. **网络优化**: 只在值发生变化时发送网络包

这个设计提供了一个优雅、可扩展、易维护的疲劳值管理系统，可以轻松适应不同的游戏需求。 