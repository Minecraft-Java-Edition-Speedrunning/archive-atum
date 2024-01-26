package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AutoResetOptionScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/items/gold_boots.png");

    @Unique
    private String difficulty;

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (Atum.isRunning) {
            Atum.createNewWorld();
        }
        this.addButton(new ButtonWidget(69, this.width / 2 - 124, this.height / 4 + 48, 20, 20, ""));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.getDifficulty();
        this.client.getTextureManager().bindTexture(BUTTON_IMAGE);
        drawTexture(this.width / 2 - 124 + 2, this.height / 4 + 48 + 2, 0.0F, 0.0F, 16, 16, 16, 16);
        if (mouseX > this.width / 2 - 124 && mouseX < this.width / 2 - 124 + 20 && mouseY > this.height / 4 + 48 && mouseY < this.height / 4 + 48 + 20 && hasShiftDown()) {
            this.drawCenteredString(client.textRenderer, difficulty, this.width / 2 - 124 + 11, this.height / 4 + 48 - 15, 16777215);
        }
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
    public void buttonClicked(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 69) {
            if (hasShiftDown()) {
                this.client.setScreen(new AutoResetOptionScreen(this));
            } else {
                Atum.scheduleReset();
            }
            ci.cancel();
        }
    }

    @Unique
    private void getDifficulty() {
        if (Atum.difficulty == -1) {
            difficulty = "Hardcore: ON";
        } else {
            difficulty = "Hardcore: OFF";
        }
    }
}
