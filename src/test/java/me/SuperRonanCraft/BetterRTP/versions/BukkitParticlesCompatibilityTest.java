package me.SuperRonanCraft.BetterRTP.versions;

import java.lang.reflect.Field;
import java.util.Locale;

/** Standalone API regression test; run with one Spigot API version on the classpath. */
public final class BukkitParticlesCompatibilityTest {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("legacy")) {
            check(!BukkitParticles.isAvailable(), "1.8 must select the legacy backend");
            System.out.println("PASS: legacy backend detection");
            return;
        }
        check(BukkitParticles.isAvailable(), "Bukkit particle API must be available");
        same("EXPLOSION_NORMAL", "POOF");
        same("EXPLOSION_LARGE", "EXPLOSION");
        same("FIREWORKS_SPARK", "FIREWORK");
        same("SPELL_WITCH", "WITCH");
        same("VILLAGER_HAPPY", "HAPPY_VILLAGER");
        same("SMOKE_NORMAL", "SMOKE");
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            same(" crit ", "CRIT");
        } finally {
            Locale.setDefault(previous);
        }
        invalid(null);
        invalid("not_a_particle");
        invalid("BLOCK_CRACK");
        invalid("REDSTONE");
        check(BukkitParticles.names().contains("CRIT"), "List must include CRIT");
        for (String name : BukkitParticles.names()) BukkitParticles.resolve(name);
        System.out.println("PASS: aliases, defaults, locale, invalid/data-dependent particles and listing ("
                + BukkitParticles.names().size() + " effects)");
    }

    private static void same(String first, String second) throws Exception {
        Field particle = BukkitParticles.Effect.class.getDeclaredField("particle");
        particle.setAccessible(true);
        check(particle.get(BukkitParticles.resolve(first)) == particle.get(BukkitParticles.resolve(second)),
                "Aliases differ: " + first + " / " + second);
    }

    private static void invalid(String name) {
        try {
            BukkitParticles.resolve(name);
            throw new AssertionError("Expected invalid particle: " + name);
        } catch (IllegalArgumentException expected) { }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
