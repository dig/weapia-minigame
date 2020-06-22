package com.weapia.survivalrealms.economy.command;

import com.weapia.survivalrealms.Constants;
import com.weapia.survivalrealms.player.SurvivalPlayer;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.core.command.BukkitCommand;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command(aliases = {"balance", "bal", "money", "cash", "coin", "coins"})
public class BalanceCommand extends BukkitCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        abstractPlayerOptional
                .map(SurvivalPlayer.class::cast)
                .ifPresent(survivalPlayer -> commandSender.sendMessage(String.format(Constants.ECONOMY_BALANCE, survivalPlayer.getCoins())));
        return false;
    }
}
