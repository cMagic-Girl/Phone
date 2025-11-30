package baios.magicgirl.phone.item;

import baios.magicgirl.phone.menu.PhoneMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EmaPhone extends Item {


    public EmaPhone() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        // 仅在服务端执行逻辑（避免客户端重复触发）
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            //Minecraft.getInstance().setScreen(new PhoneGui(new PhoneMenu(player.getInventory().selected, player.getInventory()),player.getInventory(), Component.translatable("gui.magic_girl_phone.ema_phone")));

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory,player1) -> new PhoneMenu(containerId, playerInventory, null),
                    Component.translatable("menu.title.examplemod.mymenu")
            ));

        }

        // 返回结果：成功触发，保留物品栈
        return super.use(level, player, hand);
    }

}
