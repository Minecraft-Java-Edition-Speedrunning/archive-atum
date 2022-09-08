package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AutoResetOptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;


@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private String difficulty;
    private static final Identifier BUTTON_IMAGE = new Identifier("textures/items/gold_boots.png");



    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (Atum.isRunning&& Atum.loopPrevent2) {
            Atum.loopPrevent2=false;
            client.openScreen(new CreateWorldScreen(this));
        } else {
            Atum.hotkeyState= Atum.HotkeyState.OUTSIDE_WORLD;
            method_13411(new ButtonWidget(69,this.width / 2 - 124, this.height / 4 + 48, 20, 20, ""));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlay(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        getDifficulty();
        this.client.getTextureManager().bindTexture(BUTTON_IMAGE);
        drawTexture(this.width / 2 - 124+2, this.height / 4 + 48+2, 0.0F, 0.0F, 16, 16, 16, 16);
        if (mouseX> this.width / 2 - 124&&mouseX<this.width / 2 - 124+20&&mouseY>this.height / 4 + 48&&mouseY< this.height / 4 + 48+20&&hasShiftDown()) {
            drawCenteredString(client.textRenderer, difficulty, this.width / 2 - 124+11, this.height / 4 + 48-15, 16777215);
        }
    }
    @Inject(method = "buttonClicked",at = @At("HEAD"),cancellable = true)
    public void buttonClicked(ButtonWidget button, CallbackInfo ci){
        if(button.id==69){
            if (hasShiftDown()) {
                client.openScreen(new AutoResetOptionScreen(null));
            } else {
                Atum.isRunning = true;
                MinecraftClient.getInstance().openScreen(null);
            }
            ci.cancel();
        }

    }
    private void getDifficulty() {
        if(Atum.difficulty==-1) {
            difficulty = "Hardcore: ON";
        }
        else {
            difficulty ="Hardcore: OFF";
        }

    }

}
