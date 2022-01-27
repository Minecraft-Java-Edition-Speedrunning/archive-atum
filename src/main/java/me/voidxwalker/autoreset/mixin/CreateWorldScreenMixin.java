package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.class_1157;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;


@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen{
    @Shadow private TextFieldWidget levelNameField;


    @Shadow private boolean creatingLevel;

    @Shadow private String gamemodeName;

    @Shadow private boolean structures;

    @Shadow private boolean hardcore;

    @Shadow private int generatorType;

    @Shadow public String generatorOptions;

    @Shadow private boolean bonusChest;

    @Shadow private boolean tweakedCheats;

    @Shadow private String saveDirectoryName;

    @Shadow private TextFieldWidget seedField;

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.isRunning) {
            if(Atum.isHardcore){
                hardcore=true;
            }
            levelNameField.setText((Atum.seed==null|| Atum.seed.isEmpty()?"Random":"Set")+"Speedrun #" + Atum.getNextAttempt());
            createLevel();
        }
    }
    private void createLevel (){
        this.client.openScreen((Screen)null);
        if (this.creatingLevel) {
            return;
        }

        this.creatingLevel = true;
        long l = (new Random()).nextLong();
        String string = Atum.seed;
        if (!MathHelper.method_2340(string)) {
            try {
                long var5 = Long.parseLong(string);
                if (var5 != 0L) {
                    l = var5;
                }
            } catch (NumberFormatException var7) {
                l = (long)string.hashCode();
            }
        }

        class_1157 var8 = class_1157.method_3765(this.gamemodeName);
        LevelInfo var6 = new LevelInfo(l, var8, this.structures, this.hardcore, LevelGeneratorType.TYPES[this.generatorType]);
        var6.setGeneratorOptions(this.generatorOptions);
        if (this.bonusChest && !this.hardcore) {
            var6.setBonusChest();
        }

        if (this.tweakedCheats && !this.hardcore) {
            var6.enableCommands();
        }

        this.client.startGame((Atum.seed==null|| Atum.seed.isEmpty()?"Random":"Set")+"Speedrun #" + Atum.getNextAttempt(), (Atum.seed==null|| Atum.seed.isEmpty()?"Random":"Set")+"Speedrun #" + Atum.getNextAttempt(), var6);


        Atum.log(Level.INFO,(Atum.seed==null|| Atum.seed.isEmpty()?"Resetting a random seed":"Resetting the set seed"+"\""+l+"\""));

    }
}
