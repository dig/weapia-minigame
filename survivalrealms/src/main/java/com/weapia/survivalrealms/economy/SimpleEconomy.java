package com.weapia.survivalrealms.economy;

import com.weapia.survivalrealms.player.SurvivalPlayer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.util.Symbol;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class SimpleEconomy implements Economy {

    @Inject
    private PlayerManager playerManager;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Weapia Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        BigDecimal bd = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN);
        return String.valueOf(bd.doubleValue());
    }

    @Override
    public String currencyNamePlural() {
        return ChatColor.GOLD + Symbol.LARGE_DOT;
    }

    @Override
    public String currencyNameSingular() {
        return ChatColor.GOLD + Symbol.LARGE_DOT;
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return playerManager.get(offlinePlayer.getUniqueId()).isPresent();
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return playerManager.get(player.getUniqueId()).isPresent();
        }
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return this.hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());
            if (abstractPlayerOptional.isPresent()) {
                SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
                return survivalPlayer.getCoins();
            }
        }
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(offlinePlayer.getUniqueId());
        if (abstractPlayerOptional.isPresent()) {
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            return survivalPlayer.getCoins();
        }
        return 0;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String world) {
        return this.getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());
            if (abstractPlayerOptional.isPresent()) {
                SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
                return survivalPlayer.getCoins() >= amount;
            }
        }
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(offlinePlayer.getUniqueId());
        if (abstractPlayerOptional.isPresent()) {
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            return survivalPlayer.getCoins() >= amount;
        }
        return false;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return this.has(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());
            if (abstractPlayerOptional.isPresent()) {
                SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
                survivalPlayer.setCoins(survivalPlayer.getCoins() - (int) amount);
                return new EconomyResponse(amount, survivalPlayer.getCoins(), EconomyResponse.ResponseType.SUCCESS, "");
            }
        }
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(offlinePlayer.getUniqueId());
        if (abstractPlayerOptional.isPresent()) {
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            survivalPlayer.setCoins(survivalPlayer.getCoins() - (int) amount);
            return new EconomyResponse(amount, survivalPlayer.getCoins(), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());
            if (abstractPlayerOptional.isPresent()) {
                SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
                survivalPlayer.setCoins(survivalPlayer.getCoins() + (int) amount);
                return new EconomyResponse(amount, survivalPlayer.getCoins(), EconomyResponse.ResponseType.SUCCESS, "");
            }
        }
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(offlinePlayer.getUniqueId());
        if (abstractPlayerOptional.isPresent()) {
            SurvivalPlayer survivalPlayer = (SurvivalPlayer) abstractPlayerOptional.get();
            survivalPlayer.setCoins(survivalPlayer.getCoins() + (int) amount);
            return new EconomyResponse(amount, survivalPlayer.getCoins(), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
