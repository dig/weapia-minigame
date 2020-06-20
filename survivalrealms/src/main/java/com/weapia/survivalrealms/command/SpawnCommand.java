package com.weapia.survivalrealms.command;

import com.google.inject.Inject;
import com.weapia.survivalrealms.config.WorldConfiguration;
import com.weapia.survivalrealms.player.SurvivalPlayer;
import net.sunken.common.command.Command;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.core.command.BukkitCommand;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command(aliases = {"spawn"})
public class SpawnCommand extends BukkitCommand {

    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            survivalPlayer.toPlayer().ifPresent(player -> player.teleport(worldConfiguration.getSpawn().toLocation()));
            return true;
        }
        return false;
    }

}
