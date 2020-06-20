package com.weapia.survivalrealms.player;

import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.player.Rank;
import net.sunken.core.PluginInform;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.MongoUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SurvivalPlayer extends CorePlayer {

    @Getter
    private Location lastLocation;
    private String world;

    public SurvivalPlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry, PluginInform pluginInform) {
        super(uuid, username, scoreboardRegistry, pluginInform);
        this.world = null;
        this.lastLocation = null;
    }

    @Override
    public void setup(@NonNull Player player) {
        super.setup(player);
        player.getInventory().clear();

        setScoreboard(player, scoreboard -> {
            scoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 5);
            scoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank", 4);
            scoreboard.createEntry("RankValue", rank == Rank.PLAYER ? ChatColor.RED + "No Rank" : ChatColor.valueOf(rank.getColour()) + "" + rank.getFriendlyName(), 3);
            scoreboard.createEntry("Spacer2", ChatColor.BLACK + " ", 2);
        });
    }

    @Override
    public boolean fromDocument(Document document) {
        super.fromDocument(document);
        if (document.containsKey(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY)) {
            Document doc = (Document) document.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY);
            world = doc.getString(DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY);

            if (doc.containsKey(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY)) {
                lastLocation = MongoUtil.location((Document) doc.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY));
            }
        }
        return true;
    }

    @Override
    public Document toDocument() {
        Document document = new Document()
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY, world);
        toPlayer().ifPresent(player -> document.append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY, MongoUtil.location(player.getLocation())));

        return super.toDocument()
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY, document);
    }
}
