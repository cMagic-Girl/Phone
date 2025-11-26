package baios.magicgirl.phone.item;

import baios.magicgirl.phone.GuiOpenWrapper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EmaPhone extends Item {
    public EmaPhone(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        // 仅在服务端执行逻辑（避免客户端重复触发）
        if (pLevel.isClientSide) {
            // 执行你的自定义方法
            GuiOpenWrapper.openFirstGui();
        }

        // 返回结果：成功触发，保留物品栈
        return super.use(pLevel, pPlayer, pUsedHand);
    }

}
