package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.*;
import net.minecraft.client.gui.screen.world.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.gen.WorldPreset;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Optional;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    public boolean hardcore;

    @Shadow
    @Final
    public MoreOptionsDialog moreOptionsDialog;

    @Shadow
    private TextFieldWidget levelNameField;

    @Shadow
    private Difficulty currentDifficulty;

    @Shadow
    protected abstract void createLevel();

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.isRunning) {
            Difficulty difficulty;
            switch (Atum.difficulty) {
                case 0 -> difficulty = Difficulty.PEACEFUL;
                case 1 -> difficulty = Difficulty.EASY;
                case 2 -> difficulty = Difficulty.NORMAL;
                case 3 -> difficulty = Difficulty.HARD;
                case -1 -> {
                    difficulty = Difficulty.HARD;
                    hardcore = true;
                }
                default -> {
                    Atum.log(Level.WARN, "Invalid difficulty");
                    difficulty = Difficulty.EASY;
                }
            }
            if (Atum.seed == null || Atum.seed.isEmpty()) {
                Atum.rsgAttempts++;
            } else {
                Atum.ssgAttempts++;
            }
            try {
                Atum.saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentDifficulty = difficulty;
            levelNameField.setText((Atum.seed == null || Atum.seed.isEmpty()) ? "Random Speedrun #" + Atum.rsgAttempts : "Set Speedrun #" + Atum.ssgAttempts);
            RegistryKey<WorldPreset> key = RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier(Atum.getGeneratorTypeString(Atum.generatorType)));
            Optional<RegistryEntry<WorldPreset>> entry = this.moreOptionsDialog
                    .getGeneratorOptionsHolder()
                    .dynamicRegistryManager()
                    .get(Registry.WORLD_PRESET_KEY)
                    .getEntry(key);
            entry.ifPresent(preset -> ((IMoreOptionsDialog) moreOptionsDialog).atum$setGeneratorType(preset.value()));
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateStructure(Atum.structures);
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateBonusChest(Atum.bonusChest);
            createLevel();
        }
    }
}
