package com.weapia.survivalrealms.economy.command;

import com.google.inject.Inject;
import com.weapia.survivalrealms.Constants;
import com.weapia.survivalrealms.player.SurvivalPlayer;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.PlayerManager;
import net.sunken.core.command.BukkitCommand;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command(aliases = {"addbalance"}, usage = "/addbalance <amount> [player]", rank = Rank.ADMIN, min = 1, max = 2)
public class AddBalanceCommand extends BukkitCommand {

    @Inject
    private PlayerManager playerManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (args.length == 1 && isInteger(args[0])) {
            int amount = Integer.parseInt(args[0]);
            abstractPlayerOptional
                    .map(abstractPlayer -> (SurvivalPlayer) abstractPlayer)
                    .ifPresent(survivalPlayer -> {
                        survivalPlayer.setCoins(survivalPlayer.getCoins() + amount);
                        commandSender.sendMessage(String.format(Constants.ECONOMY_BALANCE_ADD, amount, survivalPlayer.getUsername()));
                    });
            return true;
        }

        Optional<AbstractPlayer> targetOptional = playerManager.get(args[1]);
        if (args.length == 2 && isInteger(args[0]) && targetOptional.isPresent()) {
            int amount = Integer.parseInt(args[0]);
            targetOptional
                    .map(abstractPlayer -> (SurvivalPlayer) abstractPlayer)
                    .ifPresent(survivalPlayer -> {
                        survivalPlayer.setCoins(survivalPlayer.getCoins() + amount);
                        commandSender.sendMessage(String.format(Constants.ECONOMY_BALANCE_ADD, amount, survivalPlayer.getUsername()));
                    });
            return true;
        }
        return false;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
