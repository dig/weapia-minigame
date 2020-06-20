package com.weapia.survivalrealms.command;

import com.google.inject.Inject;
import com.weapia.survivalrealms.config.WorldConfiguration;
import com.weapia.survivalrealms.player.SurvivalPlayer;
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

@Command(aliases = {"adventure", "mining", "mine", "resource"}, cooldown = 500L)
public class AdventureCommand extends BukkitCommand {

    @Inject
    private PacketUtil packetUtil;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent() && !worldConfiguration.isAdventure()) {
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            AsyncHelper.executor().submit(() -> packetUtil.send(new PlayerRequestServerPacket(survivalPlayer.getUuid(), Server.Type.INSTANCE, Game.SURVIVAL_REALMS_ADVENTURE, true)));
        }
        return false;
    }
}
