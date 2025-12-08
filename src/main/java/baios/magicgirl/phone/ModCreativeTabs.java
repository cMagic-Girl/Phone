package baios.magicgirl.phone;

import baios.magicgirl.phone.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY=
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicGirlPhone.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PHONE_TAB = REGISTRY.register("magic_girl_phone_tab", () ->
            CreativeModeTab.builder()
                    // 设置 Tab 图标
                    .icon(() -> new ItemStack(ModItems.EMA_PHONE.get()))
                    // 设置 Tab 名称
                    .title(Component.translatable("creative_tab.magic_girl_phone.phone_tab"))
                    // 2.3 可选：调整 Tab 排序（比如放在「物品」Tab 之后）
                    .withTabsBefore(CreativeModeTabs.INGREDIENTS)
                    // 2.4 可选：设置 Tab 显示的搜索栏行为（默认 true 显示搜索）
                    .displayItems((parameters, output) -> {
                        // 【可选】也可以在这里直接添加物品（替代事件方式）
                        // output.accept(ModItems.EMA_PHONE.get());
                    })
                    .build()
    );

}
