package com.weapia.survivalrealms.command;

import com.google.inject.Inject;
import com.weapia.survivalrealms.config.WorldConfiguration;
import com.weapia.survivalrealms.player.Forwarder;
import com.weapia.survivalrealms.player.SurvivalPlayer;
import com.weapia.survivalrealms.world.WorldManager;
import net.sunken.common.command.Command;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.command.BukkitCommand;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Command(aliases = {"realm", "home", "island"}, cooldown = 500L)
public class RealmCommand extends BukkitCommand {

    @Inject
    private PacketUtil packetUtil;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;
    @Inject
    private WorldManager worldManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            Player player = (Player) commandSender;
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            if (worldConfiguration.isAdventure() || !worldManager.hasWorld(survivalPlayer.getUuid())) {
                survivalPlayer.setForwarder(Forwarder.REALM);
                AsyncHelper.executor().submit(() -> packetUtil.send(new PlayerRequestServerPacket(survivalPlayer.getUuid(), Server.Type.INSTANCE, Game.SURVIVAL_REALMS, true)));
            } else {
                World world = worldManager.getWorld(survivalPlayer.getUuid());
                player.teleport(world.getSpawnLocation());
            }
            return true;
        }
        return false;
    }

}
