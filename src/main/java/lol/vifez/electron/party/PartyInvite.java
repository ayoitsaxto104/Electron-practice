package lol.vifez.electron.party;

import lombok.Getter;

import java.util.UUID;

/**
 * @author axtodev
 * @project Electron
 * @website https://axto.eu
 */

@Getter
public class PartyInvite {
    private final UUID partyId;
    private final UUID inviter;
    private final UUID target;
    private final long createdAt;
    private final long expiresInMs;

    public PartyInvite(UUID partyId, UUID inviter, UUID target, long expiresInMs) {
        this.partyId = partyId;
        this.inviter = inviter;
        this.target = target;
        this.expiresInMs = expiresInMs;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > expiresInMs;
    }
}
