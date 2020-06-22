package com.weapia.survivalrealms.player;

import com.weapia.survivalrealms.Constants;
import com.weapia.survivalrealms.config.WorldConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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

    private final WorldConfiguration worldConfiguration;

    @Getter @Setter
    private Forwarder forwarder;
    @Getter
    private WorldType worldType;
    @Getter
    private Location lastLocation;

    @Getter
    private String worldLoadedInstance;
    @Getter @Setter
    private int coins;

    public SurvivalPlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry, PluginInform pluginInform, WorldConfiguration worldConfiguration) {
        super(uuid, username, scoreboardRegistry, pluginInform);
        this.worldConfiguration = worldConfiguration;

        this.forwarder = Forwarder.NONE;
        this.worldType = WorldType.SPAWN;
        this.lastLocation = null;

        this.worldLoadedInstance = null;
        this.coins = 0;
    }

    @Override
    public void setup(@NonNull Player player) {
        super.setup(player);
        player.getInventory().clear();

        setScoreboard(player, scoreboard -> {
            scoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 8);

            scoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank", 7);
            scoreboard.createEntry("RankValue", rank == Rank.PLAYER ? ChatColor.RED + "No Rank" : ChatColor.valueOf(rank.getColour()) + "" + rank.getFriendlyName(), 6);
            scoreboard.createEntry("Spacer2", ChatColor.GREEN + " ", 5);

            scoreboard.createEntry("CoinTitle", ChatColor.WHITE + "Coins", 4);
            scoreboard.createEntry("CoinValue", String.format(Constants.ECONOMY_TYPE_AMOUNT, coins), 3);
            scoreboard.createEntry("Spacer3", ChatColor.BLACK + " ", 2);
        });

        // teleport to last location
        if (!worldConfiguration.isAdventure()) {
            Location target = worldConfiguration.getSpawn().toLocation();

            if (forwarder != null && forwarder == Forwarder.SPAWN) {
                forwarder = Forwarder.NONE;
            } else if (forwarder == Forwarder.NONE && worldType != null && worldType == WorldType.SPAWN && lastLocation != null) {
                lastLocation.setWorld(target.getWorld());
                target = lastLocation;
            }

            player.teleport(target);
        }
    }

    @Override
    public boolean fromDocument(Document document) {
        super.fromDocument(document);
        if (document.containsKey(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY)) {
            Document doc = (Document) document.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY);

            forwarder = (Forwarder) MongoUtil.getEnumOrDefault(doc, Forwarder.class, DatabaseHelper.PLAYER_SURVIVAL_REALMS_FORWARDER_KEY, Forwarder.NONE);
            worldType = (WorldType) MongoUtil.getEnumOrDefault(doc, WorldType.class, DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY, WorldType.SPAWN);
            if (doc.containsKey(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY)) {
                lastLocation = MongoUtil.location((Document) doc.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY), false);
            }

            worldLoadedInstance = doc.getString(DatabaseHelper.PLAYER_SURVIVAL_REALMS_INSTANCE_KEY);
            coins = doc.getInteger(DatabaseHelper.PLAYER_SURVIVAL_REALMS_COINS_KEY, 0);
        }
        return true;
    }

    @Override
    public Document toDocument() {
        Document document = new Document()
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_FORWARDER_KEY, forwarder.toString())
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_INSTANCE_KEY, worldLoadedInstance)
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_COINS_KEY, coins);

        if (!worldConfiguration.isAdventure()) {
            toPlayer().ifPresent(player ->
                    document.append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY, MongoUtil.location(player.getLocation(), false))
                            .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY,
                                    player.getLocation().getWorld().equals(worldConfiguration.getSpawn().toLocation().getWorld()) ? WorldType.SPAWN.toString() : WorldType.REALM.toString())
            );
        }

        return super.toDocument()
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY, document);
    }
}
