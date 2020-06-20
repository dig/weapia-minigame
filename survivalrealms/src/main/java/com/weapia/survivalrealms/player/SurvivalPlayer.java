package com.weapia.survivalrealms.player;

import lombok.NonNull;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.player.Rank;
import net.sunken.core.PluginInform;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SurvivalPlayer extends CorePlayer {

    private PluginInform pluginInform;

    private String world;

    public SurvivalPlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry, PluginInform pluginInform) {
        super(uuid, username, scoreboardRegistry);
        this.pluginInform = pluginInform;
        this.world = null;
    }

    @Override
    public void setup(@NonNull Player player) {
        super.setup(player);
        player.getInventory().clear();

        CustomScoreboard customScoreboard = new CustomScoreboard(ChatColor.AQUA + "" + ChatColor.BOLD + "WEAPIA");
        customScoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 5);

        customScoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank", 4);
        customScoreboard.createEntry("RankValue", rank == Rank.PLAYER ? ChatColor.RED + "No Rank" : ChatColor.valueOf(rank.getColour()) + "" + rank.getFriendlyName(), 3);
        customScoreboard.createEntry("Spacer2", ChatColor.BLACK + " ", 2);

        customScoreboard.createEntry("ServerID", ChatColor.GRAY + pluginInform.getServer().getId(), 1);
        customScoreboard.createEntry("URL", ChatColor.LIGHT_PURPLE + "play.weapia.com", 0);

        customScoreboard.add(player);
        scoreboardRegistry.register(player.getUniqueId().toString(), customScoreboard);
    }

    @Override
    public void destroy(@NonNull Player player) {
        scoreboardRegistry.unregister(this.uuid.toString());
    }

    @Override
    public boolean fromDocument(Document document) {
        super.fromDocument(document);
        if (document.containsKey(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY)) {
            Document doc = (Document) document.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY);
            world = doc.getString(DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY);
        }
        return true;
    }

    @Override
    public Document toDocument() {
        Document document = new Document()
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY, world);
        return super.toDocument()
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY, document);
    }
}
