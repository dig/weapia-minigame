package com.weapia.survivalrealms.player;

import net.sunken.common.database.DatabaseHelper;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import org.bson.Document;

import java.util.UUID;

public class SurvivalPlayer extends CorePlayer {

    private String world;

    public SurvivalPlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry) {
        super(uuid, username, scoreboardRegistry);
        this.world = null;
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
