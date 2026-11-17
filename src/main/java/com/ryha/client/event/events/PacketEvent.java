package com.ryha.client.event.events;

import com.ryha.client.event.Event;
import net.minecraft.network.protocol.Packet;

public class PacketEvent extends Event {
    public enum Direction { SEND, RECEIVE }

    private final Packet<?> packet;
    private final Direction direction;

    public PacketEvent(Packet<?> packet, Direction direction) {
        this.packet    = packet;
        this.direction = direction;
    }

    public Packet<?> getPacket()   { return packet; }
    public Direction getDirection(){ return direction; }
    public boolean isSend()        { return direction == Direction.SEND; }
    public boolean isReceive()     { return direction == Direction.RECEIVE; }
}
