package lol.vifez.electron.party;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author axtodev
 * @project Electron
 * @website https://axto.eu
 */

@Getter
public class Party {
    private final UUID leader;
    private final Set<UUID> members = new HashSet<>();

    @Setter
    private int maxSize = 8;

    public Party(UUID leader) {
        this.leader = leader;
        this.members.add(leader);
    }

    public boolean isLeader(UUID uuid) {
        return leader.equals(uuid);
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean add(UUID uuid) {
        if (members.size() >= maxSize) return false;
        return members.add(uuid);
    }

    public boolean remove(UUID uuid) {
        return members.remove(uuid);
    }

    public Set<UUID> getAll() {
        return Collections.unmodifiableSet(members);
    }
}
