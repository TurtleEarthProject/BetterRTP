package me.SuperRonanCraft.BetterRTP.versions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Public particle API bridge, loaded reflectively to retain Minecraft 1.8 support. */
public final class BukkitParticles {
    private static final Class<?> PARTICLE = findParticleClass();

    private BukkitParticles() { }

    private static Class<?> findParticleClass() {
        try {
            return Class.forName("org.bukkit.Particle");
        } catch (ClassNotFoundException e) {
            return null; // Only 1.8 needs the legacy packet library.
        }
    }

    public static boolean isAvailable() {
        return PARTICLE != null;
    }

    public static Effect resolve(String configuredName) {
        if (configuredName == null || !isAvailable())
            throw new IllegalArgumentException("Missing particle name or Bukkit particle API");
        String name = configuredName.trim().toUpperCase(Locale.ROOT);
        Object particle = find(name);
        if (particle == null) {
            for (String[] aliases : ALIASES) {
                for (String alias : aliases) {
                    if (alias.equals(name)) {
                        for (String candidate : aliases) {
                            particle = find(candidate);
                            if (particle != null) break;
                        }
                        break;
                    }
                }
                if (particle != null) break;
            }
        }
        if (particle == null)
            throw new IllegalArgumentException("Unknown particle: " + configuredName);
        try {
            if (PARTICLE.getMethod("getDataType").invoke(particle) != Void.class)
                throw new IllegalArgumentException("Particle " + configuredName + " requires extra data, which effects.yml does not configure");
            Method spawn = Player.class.getMethod("spawnParticle", PARTICLE, Location.class,
                    int.class, double.class, double.class, double.class, double.class);
            return new Effect(particle, spawn);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot access the Bukkit particle API", e);
        }
    }

    private static Object find(String name) {
        try {
            return PARTICLE.getField(name).get(null);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Only list effects that can be displayed without additional block/color/item data. */
    public static List<String> names() {
        List<String> names = new ArrayList<>();
        if (!isAvailable()) return names;
        for (Object particle : PARTICLE.getEnumConstants()) {
            String name = ((Enum<?>) particle).name();
            try {
                resolve(name);
                names.add(name);
            } catch (IllegalArgumentException ignored) {
                // Requires data that the particle configuration cannot supply.
            }
        }
        return names;
    }

    public static final class Effect {
        private final Object particle;
        private final Method spawn;

        private Effect(Object particle, Method spawn) {
            this.particle = particle;
            this.spawn = spawn;
        }

        public void display(Location location, Vector offset, double speed, int count, Player player) {
            try {
                spawn.invoke(player, particle, location, count, offset.getX(), offset.getY(), offset.getZ(), speed);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot display Bukkit particle " + particle, e);
            }
        }
    }

    // ParticleLib / older Bukkit names and their modern Bukkit equivalents.
    private static final String[][] ALIASES = {
            {"EXPLOSION_NORMAL", "POOF"}, {"EXPLOSION_LARGE", "EXPLOSION"},
            {"EXPLOSION_HUGE", "EXPLOSION_EMITTER"}, {"FIREWORKS_SPARK", "FIREWORK"},
            {"WATER_BUBBLE", "BUBBLE"}, {"WATER_SPLASH", "SPLASH"},
            {"WATER_WAKE", "FISHING"}, {"SUSPENDED", "UNDERWATER"},
            {"SUSPENDED_DEPTH", "TOWN_AURA", "MYCELIUM"}, {"CRIT_MAGIC", "ENCHANTED_HIT"},
            {"SMOKE_NORMAL", "SMOKE"}, {"SMOKE_LARGE", "LARGE_SMOKE"},
            {"SPELL", "EFFECT"}, {"SPELL_INSTANT", "INSTANT_EFFECT"},
            {"SPELL_MOB", "ENTITY_EFFECT"}, {"SPELL_MOB_AMBIENT", "AMBIENT_ENTITY_EFFECT", "ENTITY_EFFECT"},
            {"SPELL_WITCH", "WITCH"}, {"DRIP_WATER", "DRIPPING_WATER"},
            {"DRIP_LAVA", "DRIPPING_LAVA"}, {"VILLAGER_ANGRY", "ANGRY_VILLAGER"},
            {"VILLAGER_HAPPY", "HAPPY_VILLAGER"}, {"ENCHANTMENT_TABLE", "ENCHANT"},
            {"REDSTONE", "DUST"}, {"SNOWBALL", "SNOW_SHOVEL", "ITEM_SNOWBALL"},
            {"SLIME", "ITEM_SLIME"}, {"BARRIER", "LIGHT", "BLOCK_MARKER"},
            {"WATER_DROP", "RAIN"}, {"MOB_APPEARANCE", "ELDER_GUARDIAN"},
            {"TOTEM", "TOTEM_OF_UNDYING"}, {"SPIT", "LLAMA_SPIT"},
            {"BLOCK_CRACK", "BLOCK"}, {"BLOCK_DUST", "FALLING_DUST"}, {"ITEM_CRACK", "ITEM"}
    };
}
