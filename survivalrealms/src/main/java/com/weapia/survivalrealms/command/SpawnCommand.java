package com.weapia.survivalrealms.command;

import com.google.inject.Inject;
import com.weapia.survivalrealms.config.WorldConfiguration;
import com.weapia.survivalrealms.player.SurvivalPlayer;
import com.weapia.survivalrealms.player.WorldType;
import net.sunken.common.command.Command;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.command.BukkitCommand;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command(aliases = {"spawn"}, cooldown = 500L)
public class SpawnCommand extends BukkitCommand {

    @Inject
    private PacketUtil packetUtil;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            if (worldConfiguration.isAdventure()) {
                if (survivalPlayer.getWorldType() != WorldType.SPAWN) {
                    survivalPlayer.setLastLocation(null);
                }
                survivalPlayer.setWorldType(WorldType.SPAWN);
                AsyncHelper.executor().submit(() -> packetUtil.send(new PlayerRequestServerPacket(survivalPlayer.getUuid(), Server.Type.INSTANCE, Game.SURVIVAL_REALMS, true)));
            } else {
                survivalPlayer.toPlayer().ifPresent(player -> player.teleport(worldConfiguration.getSpawn().toLocation()));
            }
            return true;
        }
        return false;
    }
}
