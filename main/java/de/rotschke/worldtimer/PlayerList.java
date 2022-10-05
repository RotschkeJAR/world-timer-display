package de.rotschke.worldtimer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class PlayerList {
    public static final PlayerList list = new PlayerList();
    public String[] names;
    public UUID[] ids;
    public long[] deaths;

    public PlayerList() {
        names = new String[0];
        ids = new UUID[0];
        deaths = new long[0];
    }

    public boolean exist(@Nonnull String name, @Nullable UUID id) {
        if (id == null) {
            for (String s : names) {
                return Objects.equals(name, s);
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                if (Objects.equals(names[i], name) && Objects.equals(ids[i], id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void add(@Nonnull String name, @Nonnull UUID id) {
        if (!exist(name, id)) {
            String[] saveNames = names;
            UUID[] saveIds = ids;
            long[] saveDeaths = deaths;
            names = new String[saveNames.length + 1];
            ids = new UUID[saveIds.length + 1];
            deaths = new long[saveDeaths.length + 1];
            for (int i = 0; i < names.length; i++) {
                names[i] = i < saveNames.length ? saveNames[i] : name;
                ids[i] = i < saveIds.length ? saveIds[i] : id;
                deaths[i] = i < saveDeaths.length ? saveDeaths[i] : 0;
            }
        }
    }

    public int getFieldByPlayer(String name, UUID id) {
        for (int i = 0; i < names.length; i++) {
            if (Objects.equals(names[i], name) && Objects.equals(ids[i], id)) {
                return i;
            }
        }
        return -1;
    }

    public long getDeathsByPlayer(String name, UUID id) {
        return deaths[getFieldByPlayer(name, id)];
    }

    public void reportDeath(String name, UUID id) {
        if (!exist(name, id)) add(name, id);
        deaths[getFieldByPlayer(name, id)] ++;
    }

    public void resetDeaths(String name, UUID id) {
        if (exist(name, id)) {
            deaths[getFieldByPlayer(name, id)] = 0;
        }
    }
}
