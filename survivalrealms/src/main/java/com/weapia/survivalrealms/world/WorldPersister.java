package com.weapia.survivalrealms.world;

import com.google.inject.*;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.*;
import com.mongodb.client.gridfs.model.*;
import net.sunken.common.database.*;
import net.sunken.common.util.*;
import org.bson.*;
import org.bukkit.entity.*;

import java.io.*;
import java.util.*;

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

    private GridFSFindIterable findWorlds(UUID playerUUID) {
        return worldBucket.find(eq("playerUUID", playerUUID.toString()));
    }

    private GridFSFile getLatestWorld(GridFSFindIterable worlds) {
        return worlds.sort(new Document("version", 1)).first();
    }

    private int getVersion(GridFSFile world) {
        return world.getMetadata().getInteger("version");
    }

    public void persistWorld(UUID playerUUID, File worldFolder) throws IOException {
        GridFSFindIterable worlds = findWorlds(playerUUID);
        int latestVersion = getVersion(getLatestWorld(worlds));
        int newVersion = ++latestVersion;

        String worldFileName = playerUUID.toString();
        String worldZipPath = worldFolder.getParent() + File.separator + worldFileName;
        ZipUtility.zip(Collections.singletonList(worldFolder), worldZipPath);
        File worldZip = new File(worldZipPath);

        InputStream streamToUploadFrom = new FileInputStream(worldZip);
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(358400)
                .metadata(new Document("playerUUID", worldFileName)
                        .append("version", newVersion));
        worldBucket.uploadFromStream(worldFileName, streamToUploadFrom, options);

        for (GridFSFile oldWorld : worlds) {
            worldBucket.delete(oldWorld.getObjectId());
        }
    }

}
