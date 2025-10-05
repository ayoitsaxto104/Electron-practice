package lol.vifez.electron.party;

import lol.vifez.electron.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author axtodev
 * @project Electron
 * @website https://axto.eu
 */

public class PartyManager {
    private final Map<UUID, Party> playerParty = new ConcurrentHashMap<>();
    private final Map<UUID, Party> partiesByLeader = new ConcurrentHashMap<>();
    private final Map<UUID, PartyInvite> pendingInvites = new ConcurrentHashMap<>();

    public Party createParty(UUID leader) {
        if (playerParty.containsKey(leader)) return playerParty.get(leader);
        Party party = new Party(leader);
        partiesByLeader.put(leader, party);
        playerParty.put(leader, party);
        return party;
    }

    public Party getParty(UUID player) {
        return playerParty.get(player);
    }

    public boolean isInParty(UUID player) {
        return getParty(player) != null;
    }

    public boolean isLeader(UUID player) {
        Party p = getParty(player);
        return p != null && p.isLeader(player);
    }

    public void disband(UUID leader) {
        Party party = partiesByLeader.remove(leader);
        if (party == null) return;
        for (UUID member : new HashSet<>(party.getAll())) {
            playerParty.remove(member);
            Player p = Bukkit.getPlayer(member);
            if (p != null) lol.vifez.electron.util.Messages.send(p, "party.disbanded");
        }
    }

    public boolean leave(UUID player) {
        Party party = getParty(player);
        if (party == null) return false;
        if (party.isLeader(player)) {
            disband(player);
            return true;
        }
        party.remove(player);
        playerParty.remove(player);
        java.util.Map<String, String> ph = new java.util.HashMap<>();
        ph.put("player", Bukkit.getOfflinePlayer(player).getName());
        broadcastKey(party, "party.left_broadcast", ph);
        return true;
    }

    public boolean transfer(UUID leader, UUID newLeader) {
        Party party = getParty(leader);
        if (party == null || !party.isLeader(leader) || !party.isMember(newLeader)) return false;
        partiesByLeader.remove(leader);
        Party newParty = new Party(newLeader);
        newParty.setMaxSize(party.getMaxSize());
        for (UUID m : party.getAll()) newParty.add(m);
        partiesByLeader.put(newLeader, newParty);
        for (UUID m : newParty.getAll()) playerParty.put(m, newParty);
        java.util.Map<String, String> ph = new java.util.HashMap<>();
        ph.put("player", Bukkit.getOfflinePlayer(newLeader).getName());
        broadcastKey(newParty, "party.transfer_broadcast", ph);
        return true;
    }

    public boolean kick(UUID leader, UUID target) {
        Party party = getParty(leader);
        if (party == null || !party.isLeader(leader)) return false;
        if (!party.isMember(target) || leader.equals(target)) return false;
        party.remove(target);
        playerParty.remove(target);
        Player t = Bukkit.getPlayer(target);
        if (t != null) lol.vifez.electron.util.Messages.send(t, "party.kicked_target");
        java.util.Map<String, String> ph = new java.util.HashMap<>();
        ph.put("player", Bukkit.getOfflinePlayer(target).getName());
        broadcastKey(party, "party.kicked_broadcast", ph);
        return true;
    }

    public void invite(UUID leader, UUID target) {
        Party party = getParty(leader);
        if (party == null || !party.isLeader(leader)) return;
        PartyInvite invite = new PartyInvite(party.getLeader(), leader, target, 60_000);
        pendingInvites.put(target, invite);
        Player t = Bukkit.getPlayer(target);
        Player l = Bukkit.getPlayer(leader);
        if (t != null) {
            java.util.Map<String, String> ph = new java.util.HashMap<>();
            ph.put("leader", l != null ? l.getName() : "Leader");
            lol.vifez.electron.util.Messages.send(t, "party.invited", ph);
        }
    }

    public boolean acceptInvite(UUID target) {
        PartyInvite invite = pendingInvites.get(target);
        if (invite == null || invite.isExpired()) {
            pendingInvites.remove(target);
            return false;
        }
        Party party = partiesByLeader.get(invite.getPartyId());
        if (party == null) return false;
        if (!party.add(target)) return false;
        playerParty.put(target, party);
        pendingInvites.remove(target);
        java.util.Map<String, String> ph = new java.util.HashMap<>();
        ph.put("player", Bukkit.getOfflinePlayer(target).getName());
        broadcastKey(party, "party.joined_broadcast", ph);
        return true;
    }

    public boolean denyInvite(UUID target) {
        PartyInvite invite = pendingInvites.remove(target);
        return invite != null;
    }

    public void broadcast(Party party, String msg) {
        for (UUID m : party.getAll()) {
            Player p = Bukkit.getPlayer(m);
            if (p != null) p.sendMessage(msg);
        }
    }

    private void broadcastKey(Party party, String key, java.util.Map<String, String> placeholders) {
        for (UUID m : party.getAll()) {
            Player p = Bukkit.getPlayer(m);
            if (p != null) lol.vifez.electron.util.Messages.send(p, key, placeholders);
        }
    }
}
