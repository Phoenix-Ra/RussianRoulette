package me.phoenixra.core.compatibility;

import java.util.Arrays;
import java.util.List;

public enum CompatibilityVersion {
    v1_8_R3,
    v1_9_R1,
    v1_12_R1,
    v1_13_R1,
    v1_16_R1;


    /*public static CompatibilityVersion current() {
        return PS_Registry.registry().compatibility().getVersion();
    }*/

    public static List<CompatibilityVersion> ordered() {
        return Arrays.asList(v1_8_R3, v1_9_R1, v1_12_R1, v1_13_R1, v1_16_R1);
    }

    public boolean atLeast(String string) {
        try {
            return this.atLeast(CompatibilityVersion.valueOf(string));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

    public boolean atLeast(CompatibilityVersion compatibilityVersion) {
        int n;
        if (this == compatibilityVersion) {
            return true;
        }
        List<CompatibilityVersion> list = CompatibilityVersion.ordered();
        for (int i = n = list.indexOf(compatibilityVersion); i < list.size(); ++i) {
            if (list.get(i) != this) continue;
            return true;
        }
        return false;
    }

    public boolean atMost(CompatibilityVersion compatibilityVersion) {
        if (this == compatibilityVersion) {
            return true;
        }
        List<CompatibilityVersion> list = CompatibilityVersion.ordered();
        int n = list.indexOf(compatibilityVersion);
        for (int i = n - 1; i >= 0; --i) {
            if (list.get(i) != this) continue;
            return true;
        }
        return false;
    }
}
