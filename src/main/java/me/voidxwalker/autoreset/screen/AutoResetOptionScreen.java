package me.voidxwalker.autoreset.screen;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;

public class AutoResetOptionScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget seedField;
    private String seed;
    private boolean isHardcore;
    private int generatorType;
    private boolean structures;
    private final String title;
    private boolean bonusChest;

    public AutoResetOptionScreen(@Nullable Screen parent) {
        super();
        title = Atum.getTranslation("menu.autoresetTitle", "Autoreset Options").asUnformattedString();
        this.parent = parent;
    }

    public void init() {
        this.isHardcore = Atum.difficulty == -1;
        this.seedField = new TextFieldWidget(350, this.client.textRenderer, this.width / 2 - 100, this.height - 160, 200, 20);
        this.seedField.setText(Atum.seed == null ? "" : Atum.seed);
        this.seedField.setFocused(true);
        this.seed = Atum.seed;
        this.generatorType = Atum.generatorType;
        this.structures = Atum.structures;
        this.bonusChest = Atum.bonusChest;

        this.addButton(new ButtonWidget(340, this.width / 2 + 5, this.height - 100, 150, 20, "Is Hardcore: " + isHardcore));
        this.addButton(new ButtonWidget(341, this.width / 2 - 155, this.height - 100, 150, 20, new TranslatableText("selectWorld.mapType").asUnformattedString() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey())));
        this.addButton(new ButtonWidget(342, this.width / 2 - 155, this.height - 64, 150, 20, new TranslatableText("selectWorld.mapFeatures").asUnformattedString() + " " + structures));
        this.addButton(new ButtonWidget(344, this.width / 2 + 5, this.height - 64, 150, 20, new TranslatableText("selectWorld.bonusItems").asUnformattedString() + " " + bonusChest));
        this.addButton(new ButtonWidget(345, this.width / 2 - 155, this.height - 28, 150, 20, Atum.getTranslation("menu.done", "Done").asUnformattedString()));
        this.addButton(new ButtonWidget(343, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
    }

    public void tick() {
        seedField.tick();
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        drawCenteredString(client.textRenderer, this.title, this.width / 2, this.height - 210, -1);
        this.drawWithShadow(client.textRenderer, "Seed (Leave empty for a random Seed)", this.width / 2 - 100, this.height - 180, -6250336);

        this.seedField.render();
        super.render(mouseX, mouseY, delta);
    }


    public void keyPressed(char character, int code) {
        if (this.seedField.isFocused()) {
            this.seedField.keyPressed(character, code);
            this.seed = this.seedField.getText();
        }
    }

    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 340:
                isHardcore = !isHardcore;
                button.message = ("Is Hardcore: " + isHardcore);
                break;
            case 341:
                generatorType++;
                if (generatorType > 5) {
                    generatorType = 0;
                }
                button.message = new TranslatableText("selectWorld.mapType").asUnformattedString() + " " + I18n.translate(LevelGeneratorType.TYPES[generatorType].getTranslationKey());
                break;
            case 342:
                structures = !structures;
                button.message = (new TranslatableText("selectWorld.mapFeatures").asUnformattedString() + " " + structures);
                break;
            case 344:
                bonusChest = !bonusChest;
                button.message = (new TranslatableText("selectWorld.bonusItems").asUnformattedString() + " " + bonusChest);
                break;
            case 345:
                Atum.seed = seed;
                Atum.difficulty = isHardcore ? -1 : 0;
                Atum.structures = structures;
                Atum.bonusChest = bonusChest;
                Atum.generatorType = generatorType;
                Atum.saveProperties();
                client.setScreen(parent);
                break;
            case 343:
                client.setScreen(parent);
                break;
        }
    }
}
