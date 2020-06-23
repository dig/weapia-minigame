package com.weapia.survivalrealms.state;

import com.google.inject.Inject;
import com.weapia.survivalrealms.Constants;
import com.weapia.survivalrealms.config.WorldConfiguration;
import com.weapia.survivalrealms.player.AdventureType;
import com.weapia.survivalrealms.player.SurvivalPlayer;
import com.weapia.survivalrealms.world.WorldManager;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.npc.NPC;
import net.sunken.core.npc.NPCRegistry;
import net.sunken.core.npc.config.InteractionConfiguration;
import net.sunken.core.npc.interact.CommandInteraction;
import net.sunken.core.npc.interact.MessageInteraction;
import net.sunken.core.npc.interact.NPCInteraction;
import net.sunken.core.npc.interact.QueueInteraction;
import net.sunken.core.util.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class GameState extends EventGameState {

    @Inject
    private WorldManager worldManager;
    @Inject
    private NPCRegistry npcRegistry;
    @Inject @InjectConfig
    private WorldConfiguration worldConfiguration;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;

    @Override
    public void start(BaseGameState previous) {
        if (!worldConfiguration.isAdventure()) {
            worldConfiguration.getNpcConfigurations()
                    .forEach(npcConfiguration -> {
                        NPC npc = npcRegistry.register(
                                npcConfiguration.getId(), npcConfiguration.getDisplayName(), npcConfiguration.getLocationConfiguration().toLocation(),
                                npcConfiguration.getSkinConfiguration().getTexture(), npcConfiguration.getSkinConfiguration().getSignature());

                        NPCInteraction npcInteraction = null;
                        InteractionConfiguration interactionConfiguration = npcConfiguration.getInteractionConfiguration();
                        switch (interactionConfiguration.getType()) {
                            case MESSAGE:
                                npcInteraction = new MessageInteraction(interactionConfiguration.getValues());
                                break;
                            case QUEUE:
                                npcInteraction = new QueueInteraction(Server.Type.valueOf(interactionConfiguration.getValues().get(0)),
                                        Game.valueOf(interactionConfiguration.getValues().get(1)),
                                        Boolean.valueOf(interactionConfiguration.getValues().get(2)),
                                        packetUtil,
                                        false);
                                break;
                            case COMMAND:
                                npcInteraction = new CommandInteraction(interactionConfiguration.getValues().get(0), bukkitSyncExecutor);
                                break;
                        }
                        npc.setInteraction(npcInteraction);
                    });
        }
    }

    @Override
    public void stop(BaseGameState next) {
    }

    @Override
    public void tick(int tickCount) {
        if (tickCount % 20 == 0 && worldConfiguration.isAdventure()) {
            Bukkit.getOnlinePlayers().forEach(player -> ActionBar.sendMessage(player, Constants.WORLD_RESOURCE_NO_BUILD));
        }
    }

    @Override
    public void onJoin(Player player) {
        playerManager.get(player.getUniqueId())
                .map(SurvivalPlayer.class::cast)
                .filter(SurvivalPlayer::isFirstJoin)
                .ifPresent(survivalPlayer -> Constants.FIRST_JOIN_MESSAGE.forEach(s -> player.sendMessage(s)));
    }

    @Override
    public void onQuit(Player player) {
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Location target = worldConfiguration.getSpawn().toLocation();
        if (worldManager.hasWorld(uuid)) {
            target = worldManager.getWorld(uuid).getSpawnLocation();
        }
        event.setRespawnLocation(target);
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return worldConfiguration.isAdventure() || player.getWorld().getName().equals(player.getUniqueId().toString());
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return worldConfiguration.isAdventure() || player.getWorld().getName().equals(player.getUniqueId().toString());
    }

    @Override
    public boolean canTakeEntityDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause) {
        return !(instigator instanceof Player);
    }

    @Override
    public boolean canDealEntityDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return !(target instanceof Player);
    }

    @Override
    public boolean canTakeDamage(Player instigator, double finalDamage, double damage) {
        return worldConfiguration.isAdventure() || instigator.getWorld().getName().equals(instigator.getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (!worldConfiguration.isAdventure()) {
            event.setCancelled(true);
            playerManager.get(player.getUniqueId())
                    .map(SurvivalPlayer.class::cast)
                    .ifPresent(survivalPlayer -> {
                        survivalPlayer.setAdventureType(AdventureType.NETHER);
                        AsyncHelper.executor().execute(() -> packetUtil.send(new PlayerRequestServerPacket(survivalPlayer.getUuid(), Server.Type.INSTANCE, Game.SURVIVAL_REALMS_ADVENTURE, true)));
                    });
        }
    }
}
