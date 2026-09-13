package me.SuperRonanCraft.BetterRTP.player.rtp.effects;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import me.SuperRonanCraft.BetterRTP.versions.BukkitParticles;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

//---
//Credit to @ByteZ1337 for ParticleLib - https://github.com/ByteZ1337/ParticleLib
//
//Use of particle creation
//---

public class RTPEffect_Particles {

    private boolean enabled;
    private final List<ParticleEffect> effects = new ArrayList<>();
    private final List<BukkitParticles.Effect> bukkitEffects = new ArrayList<>();
    private String shape;
    private final int precision = 16;

    //Some particles act very differently and might not care how they are shaped before animating, ex: EXPLOSION_NORMAL
    public static String[] shapeTypes = {
            "SCAN", //Body scan
            "EXPLODE", //Make an explosive entrance
            "TELEPORT" //Startrek type of portal
            };

    void load() {
        effects.clear();
        bukkitEffects.clear();
        FileOther.FILETYPE config = getPl().getFiles().getType(FileOther.FILETYPE.EFFECTS);
        enabled = config.getBoolean("Particles.Enabled");
        if (!enabled) return;
        //Enabled? Load all this junk
        List<String> types;
        if (config.isList("Particles.Type"))
            types = config.getStringList("Particles.Type");
        else {
            types = new ArrayList<>();
            types.add(config.getString("Particles.Type"));
        }
        String typeTrying = null;
        try {
            for (String type : types) {
                typeTrying = type;
                if (BukkitParticles.isAvailable())
                    bukkitEffects.add(BukkitParticles.resolve(type));
                else
                    effects.add(ParticleEffect.valueOf(type.toUpperCase(Locale.ROOT)));
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            effects.clear();
            bukkitEffects.clear();
            if (BukkitParticles.isAvailable())
                bukkitEffects.add(BukkitParticles.resolve("CRIT"));
            else
                effects.add(ParticleEffect.CRIT);
            getPl().getLogger().severe("The particle '" + typeTrying + "' is unavailable or needs extra data! Default particle enabled... " +
                    "Try using '/rtp info particles' to get a list of available particles");
        } catch (ExceptionInInitializerError | NoClassDefFoundError e2) {
            effects.clear();
            getPl().getLogger().severe("The particle '" + typeTrying + "' created a fatal error when loading particles! Your MC version isn't supported!");
            enabled = false;
        }
        String configuredShape = config.getString("Particles.Shape");
        shape = configuredShape == null ? shapeTypes[0] : configuredShape.toUpperCase(Locale.ROOT);
        if (!Arrays.asList(shapeTypes).contains(shape)) {
            getPl().getLogger().severe("The particle shape '" + shape + "' doesn't exist! Default particle shape enabled...");
            getPl().getLogger().severe("Try using '/rtp info shapes' to get a list of shapes, or: " + Arrays.asList(shapeTypes));
            shape = shapeTypes[0];
        }
    }

    public void display(Player p) {
        if (!enabled) return;
        AsyncHandler.syncAtEntity(p, () -> {
            if (!p.isOnline()) return;
            try { //Incase the library errors out
                switch (shape) {
                    case "TELEPORT":
                        partTeleport(p);
                        break;
                    case "EXPLODE":
                        partExplosion(p);
                        break;
                    default: //Super redundant, but... just future proofing
                    case "SCAN":
                        partScan(p);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void partScan(Player p) { //Particles with negative velocity
        Location loc = p.getLocation().add(new Vector(0, 1.75, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index);
            for (ParticleEffect effect : effects) {
                effect.display(loc.clone().add(vec), new Vector(0, -0.125, 0), .15f, 0, null, p);
            }
            for (BukkitParticles.Effect effect : bukkitEffects)
                effect.display(loc.clone().add(vec), new Vector(0, -0.125, 0), .15, 0, p);
        }
    }

    private void partTeleport(Player p) { //Static particles in a shape
        Location loc = p.getLocation();
        for (float y = 2.5f; y > 0; y -= .25f)
            for (int index = 1; index < precision; index++) {
                //double yran = ran.nextGaussian() * pHeight;
                Vector vec = getVecCircle(index).add(new Vector(0, y, 0));
                for (ParticleEffect effect : effects) {
                    effect.display(loc.clone().add(vec), p);
                }
                for (BukkitParticles.Effect effect : bukkitEffects)
                    effect.display(loc.clone().add(vec), new Vector(), 0, 1, p);
            }
    }

    private void partExplosion(Player p) { //Particles with a shape and forward velocity
        Location loc = p.getLocation().add(new Vector(0, 1, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index);
            for (ParticleEffect effect : effects) {
                effect.display(loc.clone().add(vec), vec, 1.5f, 0, null, p);
            }
            for (BukkitParticles.Effect effect : bukkitEffects)
                effect.display(loc.clone().add(vec), vec, 1.5, 0, p);
        }
    }

    private Vector getVecCircle(int index) {
        double p1 = (index * Math.PI) / (precision / 2);
        double p2 = (index - 1) * Math.PI / (precision / 2);
        //Positions
        int radius = 3;
        double x1 = Math.cos(p1) * radius;
        double x2 = Math.cos(p2) * radius;
        double z1 = Math.sin(p1) * radius;
        double z2 = Math.sin(p2) * radius;
        return new Vector(x2 - x1, 0, z2 - z1);
    }

    public static List<String> getParticleNames() {
        if (BukkitParticles.isAvailable()) return BukkitParticles.names();
        List<String> names = new ArrayList<>();
        for (ParticleEffect effect : ParticleEffect.VALUES) names.add(effect.name());
        return names;
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
