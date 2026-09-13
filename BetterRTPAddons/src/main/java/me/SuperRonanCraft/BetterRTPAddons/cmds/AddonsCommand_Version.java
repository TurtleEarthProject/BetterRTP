package me.SuperRonanCraft.BetterRTPAddons.cmds;

import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.util.Message_ADDONS;
import org.bukkit.command.CommandSender;

public class AddonsCommand_Version implements AddonsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args) {
        Message_ADDONS.sms(sendi, "&aVersion #&e" + Main.getInstance().getDescription().getVersion());
    }
}
