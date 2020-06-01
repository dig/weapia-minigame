package com.minevasion.naturaldisaster.command;

import com.google.inject.Inject;
import com.minevasion.naturaldisaster.Constants;
import com.minevasion.naturaldisaster.disaster.Disaster;
import com.minevasion.naturaldisaster.state.GameState;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.core.command.BukkitCommand;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.state.impl.BaseGameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(aliases = {"forcedisaster"}, usage = "/forcedisaster <disaster>", rank = Rank.ADMIN, max = 1)
public class ForceDisasterCommand extends BukkitCommand {

    @Inject
    private EngineManager engineManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Disasters: " + Arrays.stream(Disaster.values())
                    .map(Enum::toString)
                    .collect(Collectors.joining(",")));
        } else {
            try {
                Disaster disaster = Disaster.valueOf(args[0]);
                BaseGameState currentGameState = engineManager.getCurrentGameState();

                if (currentGameState instanceof GameState) {
                    GameState gameState = (GameState) currentGameState;
                    gameState.add(disaster);

                    commandSender.sendMessage(Constants.COMMAND_FORCEDISASTER_SUCCESS);
                } else {
                    commandSender.sendMessage(Constants.COMMAND_FORCEDISASTER_WRONG_STATE);
                }
            } catch (IllegalArgumentException ex) {
                commandSender.sendMessage(Constants.COMMAND_FORCEDISASTER_INVALID);
            }
        }

        return true;
    }

}
