package com.weapia.survivalrealms.world;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import lombok.NonNull;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoConnection;
import org.bukkit.World;

@Singleton
public class WorldPersister {

    private GridFSBucket bucket;

    @Inject
    public WorldPersister(MongoConnection mongoConnection) {
        MongoDatabase database = mongoConnection.getDatabase(DatabaseHelper.DATABASE_MAIN);
        bucket = GridFSBuckets.create(database, DatabaseHelper.GRIDFS_BUCKET_SURVIVAL_REALMS);
    }

    public void save(@NonNull World world) {

    }

    public void load(@NonNull String id) {

    }
}
