package com.weapia.survivalrealms;

import net.sunken.common.util.Symbol;
import org.bukkit.ChatColor;

public class Constants {

    public final static String CHAT_OVER_MAX_LENGTH = ChatColor.RED + "You cannot type this message as its over the 256 limit.";

    public final static String WORLD_UNLOAD = ChatColor.RED + "The world you were in has been unloaded due to the owner being offline.";
    public final static String WORLD_RESOURCE = ChatColor.RED + "Resource";
    public final static String WORLD_REALM = ChatColor.GREEN + "Realm";
    public final static String WORLD_SPAWN = ChatColor.YELLOW + "Spawn";

    public final static int ECONOMY_STARTING_AMOUNT = 500;
    public final static String ECONOMY_TYPE = ChatColor.GOLD + Symbol.LARGE_DOT;
    public final static String ECONOMY_TYPE_AMOUNT = ChatColor.GOLD + "%d" + Symbol.LARGE_DOT;

    public final static String ECONOMY_BALANCE = ChatColor.YELLOW + "You have " + ECONOMY_TYPE_AMOUNT;
    public final static String ECONOMY_BALANCE_ADD = ChatColor.GREEN + "Added " + ECONOMY_TYPE_AMOUNT + ChatColor.GREEN + " to %s.";
    public final static String ECONOMY_BALANCE_SET = ChatColor.GREEN + "Set " + ECONOMY_TYPE_AMOUNT + ChatColor.GREEN + " to %s.";

}
