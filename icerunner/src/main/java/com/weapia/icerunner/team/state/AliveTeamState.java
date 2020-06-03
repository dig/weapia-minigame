package com.weapia.icerunner.team.state;

import com.google.inject.Inject;
import com.weapia.icerunner.team.MinigameTeam;
import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.inventory.ItemBuilder;
import net.sunken.core.item.ItemRegistry;
import net.sunken.core.team.impl.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;
import java.util.UUID;

public class AliveTeamState extends BaseTeamState {

    @Inject
    private ItemRegistry itemRegistry;

    @Override
    public void start(Team team, BaseTeamState previous) {
    }

    @Override
    public void stop(Team team, BaseTeamState next) {
    }

    @Override
    public void onJoin(Team team, UUID uuid) {
        super.onJoin(team, uuid);

        Player player = Bukkit.getPlayer(uuid);
        MinigameTeam minigameTeam = (MinigameTeam) team;

        if (player != null) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(minigameTeam.getSpawn());

            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());

            PlayerInventory inventory = player.getInventory();
            inventory.clear();

            ItemStack bow = itemRegistry.getItem("bow").get().toItemStack();
            inventory.setItem(0, bow);

            ItemStack arrow = itemRegistry.getItem("arrow").get().toItemStack();
            inventory.setItem(17, arrow);

            ItemStack snowball = itemRegistry.getItem("snowball").get().toItemStack();
            inventory.setItem(1, snowball);
        }
    }

    @Override
    public void onQuit(Team team, UUID uuid) {
        super.onQuit(team, uuid);
    }

}
