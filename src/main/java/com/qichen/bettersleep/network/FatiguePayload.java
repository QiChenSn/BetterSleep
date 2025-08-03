package com.qichen.bettersleep.network;

import com.qichen.bettersleep.BetterSleep;
import com.qichen.bettersleep.data.FatigueData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid=BetterSleep.MODID)
public class FatiguePayload implements CustomPacketPayload {
    private final float fatigue;
    public FatiguePayload(float fatigue){
        this.fatigue=fatigue;
    }

    public float getFatigue(){return this.fatigue;}

    public static final CustomPacketPayload.Type<FatiguePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BetterSleep.MODID, "fatigue"));

    public static final StreamCodec<ByteBuf, FatiguePayload> STREAM_CODEC = StreamCodec.composite(             // 从MyData中获取name的getter方法
            ByteBufCodecs.FLOAT,      // 编码/解码age（整数类型）
            FatiguePayload::getFatigue,                // 从MyData中获取age的getter方法
            FatiguePayload::new                 // 用解析出的name和age构造MyData对象
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // 1. 获取注册器，并指定网络版本（如"1"，用于版本兼容）
        PayloadRegistrar registrar = event.registrar("1");

        // 2. 注册Payload：指定传输阶段、方向、类型、编解码器和处理器
        registrar.playBidirectional(  // 游戏“运行阶段”双向传输（客户端↔服务器）
                FatiguePayload.TYPE,              // 关联Payload的唯一标识
                FatiguePayload.STREAM_CODEC,      // 关联编解码器
                // 3. 处理器：区分客户端和服务器的处理逻辑
                new DirectionalPayloadHandler<>(
                        FatigueClientPayloadHandler::handleData,  // 客户端处理器
                        FatigueServerPayloadHandler::handleData   // 服务器处理器
                )
        );
    }
}
