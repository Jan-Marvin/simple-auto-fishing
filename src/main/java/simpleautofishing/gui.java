package simpleautofishing;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class gui extends Screen  {

    public gui() {
        super(Component.translatable("text.simpleautofishing.gui.name"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(
                new net.minecraft.client.gui.components.StringWidget(
                        centerX - (this.font.width(this.title) / 2), centerY - 90, 200, 20,
                        this.title,
                        this.font
                )
        );

        Button enableToggleButton = Button.builder(getEnableText(), button -> {
                    simpleautofishing.enabled = !simpleautofishing.enabled;
                    button.setMessage(getEnableText());
                })
                .bounds(centerX - 45, centerY - 60, 90, 20)
                .build();


        Button modeToggleButton = Button.builder(getModeEnableText(), button -> {
                    simpleautofishing.FishingRodMode = simpleautofishing.FishingRodMode.next();
                    button.setMessage(getModeEnableText());
                })
                .bounds(centerX - (Math.max(this.font.width(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar_protected")) + 16, 90)/2), centerY - 30, Math.max(this.font.width(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar_protected")) + 16, 90), 20)
                .build();

        AbstractSliderButton delaySlider = new AbstractSliderButton(
                centerX - 45, centerY, 100, 20,
                (Component.translatable("text.simpleautofishing.gui.delay")
                        .append(": " + simpleautofishing.recastDelayTicks)
                ),
                simpleautofishing.recastDelayTicks / 100.0
        ) {
            @Override
            protected void updateMessage() {
                int ticks = (int)(value * 100);
                setMessage((Component.translatable("text.simpleautofishing.gui.delay")
                        .append(": " + ticks)
                ));
            }

            @Override
            protected void applyValue() {
                simpleautofishing.recastDelayTicks = (int)(value * 100);
            }
        };

        Button enableAttackHotkeyButton = Button.builder(getenableAttackHotkeyText(), button -> {
                    simpleautofishing.enableAttackHotkey = !simpleautofishing.enableAttackHotkey;
                    button.setMessage(getenableAttackHotkeyText());
                })
                .bounds(centerX - 65, centerY + 30, 130, 20)
                .build();

        this.addRenderableWidget(enableToggleButton);
        this.addRenderableWidget(modeToggleButton);
        this.addRenderableWidget(delaySlider);
        this.addRenderableWidget(enableAttackHotkeyButton);

        this.addRenderableWidget(
                Button.builder(Component.translatable("text.simpleautofishing.gui.close"), btn -> this.onClose())
                        .bounds(centerX - 40, centerY + 60, 80, 20)
                        .build()
        );
    }

    private Component getenableAttackHotkeyText() {
        if (simpleautofishing.enableAttackHotkey) {
            return (Component.translatable("text.simpleautofishing.gui.hotkeyEnabled")
            );
        } else if (!simpleautofishing.enableAttackHotkey) {
            return (Component.translatable("text.simpleautofishing.gui.hotkeyDisabled")
            );
        }

        return Component.literal("AttackHotkey error");
    }

    private Component getModeEnableText(){
        if (simpleautofishing.FishingRodMode == simpleautofishing.FishingRodModes.fishingRodUnprotected) {
            return (Component.translatable("text.simpleautofishing.safMode.mode")
                    .append(": ")
                    .append(Component.translatable("text.simpleautofishing.safMode.fishing_rod_unprotected"))
            );
        } else if (simpleautofishing.FishingRodMode == simpleautofishing.FishingRodModes.fishingRodProtected) {
            return (Component.translatable("text.simpleautofishing.safMode.mode")
                    .append(": ")
                    .append(Component.translatable("text.simpleautofishing.safMode.fishing_rod_protected"))
            );
        } else if (simpleautofishing.FishingRodMode == simpleautofishing.FishingRodModes.allInHotbar) {
            return (Component.translatable("text.simpleautofishing.safMode.mode")
                    .append(": ")
                    .append(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar"))
            );
        } else if (simpleautofishing.FishingRodMode == simpleautofishing.FishingRodModes.allInHotbarProtected) {
            return (Component.translatable("text.simpleautofishing.safMode.mode")
                    .append(": ")
                    .append(Component.translatable("text.simpleautofishing.safMode.all_in_hotbar_protected"))
            );
        }
        return Component.literal("mode error");
    }

    private Component getEnableText() {
        return simpleautofishing.enabled
                ? Component.translatable("text.simpleautofishing.cmd.enabled")
                : Component.translatable("text.simpleautofishing.cmd.disabled");
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}