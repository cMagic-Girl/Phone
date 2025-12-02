package baios.magicgirl.phone.sound;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import org.jetbrains.annotations.Nullable;


@EventBusSubscriber(modid = "magic_girl_phone", value = Dist.CLIENT)
public class SoundRecorder {

    @SubscribeEvent
    public static void SoundEvent(PlayLevelSoundEvent event) {
        Vec3 soundPos = getSoundSourcePosition(event);
        if (soundPos != null) {
            // 输出声音位置信息
            //System.out.printf("声音位置：X=%.2f, Y=%.2f, Z=%.2f%n",
                    //soundPos.x, soundPos.y, soundPos.z);

            // 可结合玩家位置计算距离
            // Vec3 playerPos = Minecraft.getInstance().player.position();
            // double distance = soundPos.distanceTo(playerPos);
        }
        @Nullable Holder<SoundEvent> sound = event.getSound();
    }

    public static Vec3 getSoundSourcePosition(PlayLevelSoundEvent event) {
        if (event instanceof PlayLevelSoundEvent.AtEntity atEntityEvent) {
            // 实体发出的声音：返回实体的位置
            return atEntityEvent.getEntity().position();
        } else if (event instanceof PlayLevelSoundEvent.AtPosition atPositionEvent) {
            // 固定位置发出的声音：返回预定义位置
            return atPositionEvent.getPosition();
        } else {
            // 未知事件类型（极少出现）
            return null;
        }
    }
}
