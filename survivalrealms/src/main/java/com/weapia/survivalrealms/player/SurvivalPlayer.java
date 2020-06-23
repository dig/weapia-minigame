package com.weapia.survivalrealms.player;

import com.weapia.survivalrealms.Constants;
import com.weapia.survivalrealms.config.WorldConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.player.Rank;
import net.sunken.common.util.MongoUtil;
import net.sunken.core.PluginInform;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.MongoBukkitUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SurvivalPlayer extends CorePlayer {

    private final WorldConfiguration worldConfiguration;

    @Getter @Setter
    private AdventureType adventureType;
    @Getter @Setter
    private WorldType worldType;
    @Getter @Setter
    private Location lastLocation;

    private Document lastPlayerState;
    @Getter
    private String worldLoadedInstance;
    @Getter @Setter
    private int coins;

    public SurvivalPlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry, PluginInform pluginInform, WorldConfiguration worldConfiguration) {
        super(uuid, username, scoreboardRegistry, pluginInform);
        this.worldConfiguration = worldConfiguration;

        this.adventureType = AdventureType.OVERWORLD;
        this.worldType = WorldType.SPAWN;
        this.lastLocation = null;

        this.lastPlayerState = null;
        this.worldLoadedInstance = null;
        this.coins = Constants.ECONOMY_STARTING_AMOUNT;
    }

    @Override
    public void setup(@NonNull Player player) {
        super.setup(player);

        player.getInventory().clear();
        if (lastPlayerState != null) {
            MongoBukkitUtil.setPlayer(player, lastPlayerState);
        }

        Location target;
        if (!worldConfiguration.isAdventure()) {
            target = worldConfiguration.getSpawn().toLocation();
            if (worldType == WorldType.SPAWN && lastLocation != null) {
                lastLocation.setWorld(target.getWorld());
                target = lastLocation;
            }
        } else {
            target = Bukkit.getWorld("world").getSpawnLocation();
            if (adventureType == AdventureType.NETHER) {
                target = Bukkit.getWorld("world_nether").getSpawnLocation();
            }
        }
        player.teleport(target);
    }

    @Override
    protected boolean setupScoreboard(@NonNull CustomScoreboard scoreboard) {
        scoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 11);

        scoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank", 10);
        scoreboard.createEntry("RankValue", rank == Rank.PLAYER ? ChatColor.RED + "No Rank" : ChatColor.valueOf(rank.getColour()) + "" + rank.getFriendlyName(), 9);
        scoreboard.createEntry("Spacer2", ChatColor.GREEN + " ", 8);

        scoreboard.createEntry("CoinTitle", ChatColor.WHITE + "Coins", 7);
        scoreboard.createEntry("CoinValue", String.format(Constants.ECONOMY_TYPE_AMOUNT, coins), 6);
        scoreboard.createEntry("Spacer3", ChatColor.BLACK + " ", 5);

        scoreboard.createEntry("WorldTitle", ChatColor.WHITE + "World", 4);
        scoreboard.createEntry("WorldValue", worldConfiguration.isAdventure() ? Constants.WORLD_RESOURCE : (worldType == WorldType.REALM ? Constants.WORLD_REALM : Constants.WORLD_SPAWN), 3);
        scoreboard.createEntry("Spacer4", ChatColor.GOLD + " ", 2);
        return true;
    }

    @Override
    public boolean fromDocument(Document document) {
        super.fromDocument(document);
        if (document.containsKey(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY)) {
            Document doc = (Document) document.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY);

            adventureType = (AdventureType) MongoUtil.getEnumOrDefault(doc, AdventureType.class, DatabaseHelper.PLAYER_SURVIVAL_REALMS_ADVENTURE_KEY, AdventureType.OVERWORLD);
            worldType = (WorldType) MongoUtil.getEnumOrDefault(doc, WorldType.class, DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY, WorldType.SPAWN);
            if (doc.containsKey(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY)) {
                lastLocation = MongoBukkitUtil.toLocation((Document) doc.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY));
            }

            lastPlayerState = (Document) doc.get(DatabaseHelper.PLAYER_SURVIVAL_REALMS_PLAYER_KEY);
            worldLoadedInstance = doc.getString(DatabaseHelper.PLAYER_SURVIVAL_REALMS_INSTANCE_KEY);
            coins = doc.getInteger(DatabaseHelper.PLAYER_SURVIVAL_REALMS_COINS_KEY, Constants.ECONOMY_STARTING_AMOUNT);
        }
        return true;
    }

    @Override
    public Document toDocument(@NonNull Player player) {
        Document document = new Document()
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_INSTANCE_KEY, worldLoadedInstance)
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_COINS_KEY, coins);

        if (!worldConfiguration.isAdventure()) {
            document.append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY,
                    player.getLocation().getWorld().equals(worldConfiguration.getSpawn().toLocation().getWorld()) ? WorldType.SPAWN.toString() : WorldType.REALM.toString())
                    .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_ADVENTURE_KEY, adventureType.toString())
                    .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY, MongoBukkitUtil.fromLocation(player.getLocation(), false));
        } else {
            World world = player.getLocation().getWorld();
            document.append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_WORLD_KEY, worldType.toString())
                    .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_ADVENTURE_KEY, world.getName().equals("world") ? AdventureType.OVERWORLD.toString() : AdventureType.NETHER.toString());
            if (lastLocation != null) {
                document.append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_LOCATION_KEY, MongoBukkitUtil.fromLocation(lastLocation, false));
            }
        }

        document.append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_PLAYER_KEY, MongoBukkitUtil.fromPlayer(player, false, false));
        return super.toDocument(player)
                .append(DatabaseHelper.PLAYER_SURVIVAL_REALMS_KEY, document);
    }
}
