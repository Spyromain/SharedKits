package me.spyromain.bukkit.sharedkits.gui.window;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import me.spyromain.bukkit.sharedkits.gui.GUIPlayer;
import me.spyromain.bukkit.sharedkits.gui.GUIUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIWindow;

public class YesNoWindow implements GUIWindow {
    public static final int INFO_SLOT = 13;
    public static final int NO_SLOT = 29;
    public static final int YES_SLOT = 33;

    private final GUIPlayer guiPlayer;
    private final String yesAnswer;
    private final String noAnswer;
    private final Runnable yesAction;
    private final Runnable noAction;
    private final String question;
    private final String[] questionLore;

    public YesNoWindow(GUIPlayer guiPlayer, Runnable yesAction, Runnable noAction, String question, String... questionLore) {
        this(guiPlayer, "Yes", "No", yesAction, noAction, question, questionLore);
    }

    public YesNoWindow(GUIPlayer guiPlayer, String yesAnswer, String noAnswer, Runnable yesAction, Runnable noAction, String question, String... questionLore) {
        this.guiPlayer = guiPlayer;
        this.yesAnswer = yesAnswer;
        this.noAnswer = noAnswer;
        this.yesAction = yesAction;
        this.noAction = noAction;
        this.question = question;
        this.questionLore = questionLore;
    }

    @Override
    public void init() {
        Inventory inventory = guiPlayer.getInventory();

        inventory.setItem(INFO_SLOT, GUIUtils.newIcon(
            Material.BOOK,
            question,
            questionLore
        ));
        inventory.setItem(NO_SLOT, GUIUtils.newIcon(
            Material.STAINED_CLAY,
            (short) 14,
            noAnswer
        ));
        inventory.setItem(YES_SLOT, GUIUtils.newIcon(
            Material.STAINED_CLAY,
            (short) 5,
            yesAnswer
        ));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            if (event.getSlot() == NO_SLOT) {
                noAction.run();
            }
            else if (event.getSlot() == YES_SLOT) {
                yesAction.run();
            }
        }
    }
}
