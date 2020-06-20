package com.weapia.survivalrealms.world;

import com.google.inject.*;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.*;
import com.mongodb.client.gridfs.model.*;
import net.sunken.common.database.*;
import org.bson.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.io.*;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class WorldPersister {

    private final MongoDatabase mainDatabase;
    private final GridFSBucket worldBucket;

    @Inject
    WorldPersister(MongoConnection mongoConnection) {
        this.mainDatabase = mongoConnection.getDatabase(DatabaseHelper.DATABASE_MAIN);
        this.worldBucket = GridFSBuckets.create(mainDatabase, DatabaseHelper.GRIDFS_BUCKET_SURVIVAL_REALMS);
    }

//    @Nullable
//    public GridFSFile getWorld(Player player) {
//        return findWorlds(player).first();
//    }

    private GridFSFindIterable findWorlds(Player player) {
        return worldBucket.find(eq("playerUUID", player.getUniqueId().toString()));
    }

    private GridFSFile getLatestWorld(GridFSFindIterable worlds) {
        return worlds.sort(new Document("version", 1)).first();
    }

    private int getVersion(GridFSFile world) {
        return world.getMetadata().getInteger("version");
    }

    public void persistWorld(Player player, World world) throws FileNotFoundException {
        String playerUUID = player.getUniqueId().toString();

        GridFSFindIterable worlds = findWorlds(player);
        int latestVersion = getVersion(getLatestWorld(worlds));
        int newVersion = ++latestVersion;

        File worldFolder = world.getWorldFolder();
        File worldZip = worldFolder; // TODO: zip

        InputStream streamToUploadFrom = new FileInputStream(worldZip);
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(358400)
                .metadata(new Document("playerUUID", playerUUID)
                        .append("version", newVersion));
        worldBucket.uploadFromStream(playerUUID, streamToUploadFrom, options);

        for (GridFSFile oldWorld : worlds) {
            worldBucket.delete(oldWorld.getObjectId());
        }
    }

}
