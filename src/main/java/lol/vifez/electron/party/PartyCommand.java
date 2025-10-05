package lol.vifez.electron.party;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.util.Messages;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("party|p")
public class PartyCommand extends BaseCommand {

    @Default
    public void help(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage("§b§lParty Commands");
        sender.sendMessage(" ");
        sender.sendMessage("§7▪ §b/party create §7- §fCreate a party");
        sender.sendMessage("§7▪ §b/party invite <player> §7- §fInvite a player");
        sender.sendMessage("§7▪ §b/party accept §7- §fAccept party invite");
        sender.sendMessage("§7▪ §b/party deny §7- §fDeny party invite");
        sender.sendMessage("§7▪ §b/party leave §7- §fLeave your party");
        sender.sendMessage("§7▪ §b/party disband §7- §fDisband your party");
        sender.sendMessage("§7▪ §b/party kick <player> §7- §fKick a member");
        sender.sendMessage("§7▪ §b/party transfer <player> §7- §fTransfer leadership");
        sender.sendMessage("§7▪ §b/party list §7- §fList members");
        sender.sendMessage(" ");
    }

    private PartyManager pm() { return Practice.getInstance().getPartyManager(); }

    @Subcommand("create")
    @CommandPermission("electron.default")
    public void create(Player sender) {
        if (pm().isInParty(sender.getUniqueId())) {
            Messages.send(sender, "party.already_in");
            return;
        }
        pm().createParty(sender.getUniqueId());
        Messages.send(sender, "party.created");
    }

    @Subcommand("invite")
    public void invite(Player sender, @Single String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) { Messages.send(sender, "general.player_not_found"); return; }
        if (!pm().isLeader(sender.getUniqueId())) { Messages.send(sender, "party.must_be_leader"); return; }
        if (pm().isInParty(target.getUniqueId())) { Messages.send(sender, "party.already_in_party"); return; }
        pm().invite(sender.getUniqueId(), target.getUniqueId());
        java.util.Map<String, String> ph = new java.util.HashMap<>();
        ph.put("target", target.getName());
        Messages.send(sender, "party.invite_sent", ph);
    }

    @Subcommand("accept")
    public void accept(Player sender) {
        if (pm().isInParty(sender.getUniqueId())) { Messages.send(sender, "party.already_in"); return; }
        if (pm().acceptInvite(sender.getUniqueId())) {
            Messages.send(sender, "party.joined");
        } else Messages.send(sender, "party.no_invite");
    }

    @Subcommand("deny")
    public void deny(Player sender) {
        if (pm().denyInvite(sender.getUniqueId())) Messages.send(sender, "party.invite_denied");
        else Messages.send(sender, "party.no_invite");
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        if (pm().leave(sender.getUniqueId())) Messages.send(sender, "party.leave_self");
        else Messages.send(sender, "party.not_in");
    }

    @Subcommand("disband")
    public void disband(Player sender) {
        if (!pm().isLeader(sender.getUniqueId())) { Messages.send(sender, "party.must_be_leader"); return; }
        pm().disband(sender.getUniqueId());
    }

    @Subcommand("kick")
    public void kick(Player sender, @Single String targetName) {
        if (!pm().isLeader(sender.getUniqueId())) { Messages.send(sender, "party.must_be_leader"); return; }
        Player target = Bukkit.getPlayerExact(targetName);
        UUID targetId = target != null ? target.getUniqueId() : null;
        if (targetId == null) { Messages.send(sender, "general.player_not_found"); return; }
        if (pm().kick(sender.getUniqueId(), targetId)) {
            java.util.Map<String, String> ph = new java.util.HashMap<>();
            ph.put("player", targetName);
            Messages.send(sender, "party.kicked_broadcast", ph);
        } else Messages.send(sender, "party.kick_failed");
    }

    @Subcommand("transfer")
    public void transfer(Player sender, @Single String targetName) {
        if (!pm().isLeader(sender.getUniqueId())) { Messages.send(sender, "party.must_be_leader"); return; }
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) { Messages.send(sender, "general.player_not_found"); return; }
        if (pm().transfer(sender.getUniqueId(), target.getUniqueId())) Messages.send(sender, "party.transfer_success");
        else Messages.send(sender, "party.transfer_failed");
    }

    @Subcommand("list")
    public void list(Player sender) {
        Party party = pm().getParty(sender.getUniqueId());
        if (party == null) { Messages.send(sender, "party.not_in"); return; }
        String members = party.getAll().stream()
                .map(id -> {
                    Player p = Bukkit.getPlayer(id);
                    String name = p != null ? p.getName() : Bukkit.getOfflinePlayer(id).getName();
                    if (party.getLeader().equals(id)) return Messages.format(Messages.raw("party.list_leader_format"), java.util.Collections.singletonMap("player", name));
                    return Messages.format(Messages.raw("party.list_member_format"), java.util.Collections.singletonMap("player", name));
                })
                .collect(Collectors.joining(CC.translate("&7, ")));
        sender.sendMessage(Messages.format(Messages.raw("party.list_header"), null) + " " + members);
    }
}
