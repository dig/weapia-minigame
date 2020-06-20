package com.weapia.survivalrealms.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

@Getter
@AllArgsConstructor
public class PlayerChatPacket extends Packet {

    private final PlayerDetail player;
    private final String format;
    private final String message;

}
