package me.p0n41k.artifactworld.power;

import me.p0n41k.artifactworld.Artifactworld;
import me.p0n41k.artifactworld.NoManaPlayer;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.power.Mechanics.CooldownManager;
import me.p0n41k.artifactworld.power.Mechanics.Silence;
import me.p0n41k.artifactworld.power.Mechanics.SoundUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.p0n41k.artifactworld.data.ManaMechanics;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;


public class Spellcaster implements Listener {
    private final JavaPlugin plugin;
    private final ArtifactMenu artifactMenu;
    private static final int spellCasterCooldownTime = 7;
    private static final int manauseg = 40;
    private final int cmduseg = 115;
    private static final CooldownManager spellCasterCooldownManager = new CooldownManager();

    private Map<UUID, String> playerOneSphere = new HashMap<>();
    private Map<UUID, String> playerTwoSphere = new HashMap<>();
    private Map<UUID, String> playerThreeSphere = new HashMap<>();
    private final Map<UUID, Float> sneakingPlayers = new HashMap<>();

    public void setPlayerOneSphere(UUID playerId, String value) {
        playerOneSphere.put(playerId, value);
    }

    public void setPlayerTwoSphere(UUID playerId, String value) {
        playerTwoSphere.put(playerId, value);
    }

    public void setPlayerThreeSphere(UUID playerId, String value) {
        playerThreeSphere.put(playerId, value);
    }

    // Функции для получения значений

    public String getPlayerOneSphere(UUID playerId) {
        return playerOneSphere.getOrDefault(playerId, "-");
    }

    public String getPlayerTwoSphere(UUID playerId) {
        return playerTwoSphere.getOrDefault(playerId, "-");
    }

    public String getPlayerThreeSphere(UUID playerId) {
        return playerThreeSphere.getOrDefault(playerId, "-");
    }

    public Spellcaster(JavaPlugin plugin) {
        this.plugin = plugin;
        this.artifactMenu = new ArtifactMenu((Artifactworld) plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                checkSlot();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public static void resetAllCooldownsSpellCaster(Player player) {
        spellCasterCooldownManager.resetCooldown(player.getName());
    }

    public static ItemStack createSpellCasterItem() {
        ItemStack s13 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta s13meta = s13.getItemMeta();
        s13meta.setDisplayName(ChatColor.YELLOW + "Spell Caster");
        s13meta.setCustomModelData(115);
        List<String> lores13 = new ArrayList<>();
        lores13.add(ChatColor.BLUE + "   Создает сгустки энергии, при");
        lores13.add(ChatColor.BLUE + "   комбинировании которых выдает");
        lores13.add(ChatColor.BLUE + "   временный эффект зелья.");
        lores13.add("");
        lores13.add(ChatColor.AQUA + "   Мана: " + ChatColor.GRAY + manauseg);
        lores13.add(ChatColor.RED + "   Перезарядка: " + ChatColor.GRAY + spellCasterCooldownTime);
        lores13.add("");
        s13meta.setLore(lores13);
        s13.setItemMeta(s13meta);
        return s13;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellCasterCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleSpellCaster(player);
                    manaMechanics.addSecondMana(-manauseg);
                    spellCasterCooldownManager.setCooldown(player.getName(), spellCasterCooldownTime);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        }

    }



    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellCasterCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleSpellCaster(player);
                    manaMechanics.addSecondMana(-manauseg);
                    spellCasterCooldownManager.setCooldown(player.getName(), spellCasterCooldownTime);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        } else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellCasterCooldownManager.hasCooldown(player.getName())) {
                if (Silence.getSilenceState(player)>= 1) {
                    event.setCancelled(true);
                    NoManaPlayer.displaySilenceMessage(player);
                    return;
                }
                if (secondManaUse >= manauseg) {
                    event.setCancelled(true);
                    handleSpellCaster(player);
                    manaMechanics.addSecondMana(-manauseg);
                    spellCasterCooldownManager.setCooldown(player.getName(), spellCasterCooldownTime);
                }
                else {
                    NoManaPlayer.displayNoManaMessage(player);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        ManaMechanics manaMechanics = new ManaMechanics(player.getUniqueId());
        int secondManaUse = manaMechanics.getSecondMana();
        if (player.getInventory().getHeldItemSlot() == hasArtifact-9 && !spellCasterCooldownManager.hasCooldown(player.getName())) {
            if (Silence.getSilenceState(player)>= 1) {
                event.setCancelled(true);
                NoManaPlayer.displaySilenceMessage(player);
                return;
            }
            if (secondManaUse >= manauseg) {
                event.setCancelled(true);
                handleSpellCaster(player);
                manaMechanics.addSecondMana(-manauseg);
                spellCasterCooldownManager.setCooldown(player.getName(), spellCasterCooldownTime);
            }
            else {
                NoManaPlayer.displayNoManaMessage(player);
            }
        }
    }


    public void checkSlot() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            String actionBarMessage = "";

            Integer hasArtifact = artifactMenu.checkArtifactMenu(onlinePlayer, cmduseg);
            List<Integer> getspellslots =  artifactMenu.getDiamondHorseArmorSlots(onlinePlayer);

            if (hasArtifact == null) {
                continue;
            }

            String oneSphere = getPlayerOneSphere(onlinePlayer.getUniqueId());
            String twoSphere = getPlayerTwoSphere(onlinePlayer.getUniqueId());
            String threeSphere = getPlayerThreeSphere(onlinePlayer.getUniqueId());

            String cast;

            if (oneSphere.equals("A") && twoSphere.equals("A") && threeSphere.equals("A")) {
                cast = "Прыгучесть ";
            } else if (oneSphere.equals("A") && twoSphere.equals("A") && threeSphere.equals("B") || oneSphere.equals("A") && twoSphere.equals("B") && threeSphere.equals("A") || oneSphere.equals("B") && twoSphere.equals("A") && threeSphere.equals("A")) {
                cast = "Плавное падение ";
            } else if (oneSphere.equals("A") && twoSphere.equals("A") && threeSphere.equals("G") || oneSphere.equals("A") && twoSphere.equals("G") && threeSphere.equals("A") || oneSphere.equals("G") && twoSphere.equals("A") && threeSphere.equals("A")) {
                cast = "Сила ";
            } else if (oneSphere.equals("A") && twoSphere.equals("B") && threeSphere.equals("B") || oneSphere.equals("B") && twoSphere.equals("A") && threeSphere.equals("B") || oneSphere.equals("B") && twoSphere.equals("B") && threeSphere.equals("A")) {
                cast = "Огнестойкость ";
            } else if (oneSphere.equals("A") && twoSphere.equals("G") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("A") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("G") && threeSphere.equals("A")) {
                cast = "Спешка ";
            } else if (oneSphere.equals("B") && twoSphere.equals("B") && threeSphere.equals("B")) {
                cast = "Скорость ";
            } else if (oneSphere.equals("B") && twoSphere.equals("B") && threeSphere.equals("G") || oneSphere.equals("B") && twoSphere.equals("G") && threeSphere.equals("B") || oneSphere.equals("G") && twoSphere.equals("B") && threeSphere.equals("B")) {
                cast = "Грация дельфина ";
            } else if (oneSphere.equals("B") && twoSphere.equals("G") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("B") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("G") && threeSphere.equals("B")) {
                cast = "Водное дыхание ";
            } else if (oneSphere.equals("G") && twoSphere.equals("G") && threeSphere.equals("G")) {
                cast = "Сопротивление ";
            } else if (oneSphere.equals("A") && twoSphere.equals("B") && threeSphere.equals("G") || oneSphere.equals("A") && twoSphere.equals("G") && threeSphere.equals("B") || oneSphere.equals("B") && twoSphere.equals("A") && threeSphere.equals("G") || oneSphere.equals("B") && twoSphere.equals("G") && threeSphere.equals("A") || oneSphere.equals("G") && twoSphere.equals("A") && threeSphere.equals("B") || oneSphere.equals("G") && twoSphere.equals("B") && threeSphere.equals("A")) {
                cast = "Невидимость ";
            } else {
                cast = "Не известно ";
            }

            int heldItemSlot = onlinePlayer.getInventory().getHeldItemSlot();
            if (heldItemSlot == hasArtifact-9) {
                if (spellCasterCooldownManager.hasCooldown(onlinePlayer.getName())) {
                    int remainingTime = (int) ((spellCasterCooldownManager.cooldowns.get(onlinePlayer.getName()) - System.currentTimeMillis()) / 1000);
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + ""  + manauseg + ChatColor.GRAY + " | " + ChatColor.RED + cast + oneSphere + " " + twoSphere + " " + threeSphere + ChatColor.GRAY + " | КД: " + remainingTime + " сек.");
                } else {
                    sendActionBarMessage(onlinePlayer, ChatColor.AQUA + "" + manauseg + ChatColor.GRAY + " | " + ChatColor.YELLOW + cast + oneSphere + " " + twoSphere + " " + threeSphere + ChatColor.GRAY + " |");
                }
            } else if (!getspellslots.contains(heldItemSlot)) {
                sendActionBarMessage(onlinePlayer, " ");
            }

            sendActionBarMessage(onlinePlayer, actionBarMessage);
        }
    }


    private void sendActionBarMessage(Player player, String message) {
        player.sendActionBar(message);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        Integer hasArtifact = artifactMenu.checkArtifactMenu(player, cmduseg);
        if (hasArtifact == null) {
            sendActionBarMessage(player, " ");
            return;
        }

        int heldItemSlot = player.getInventory().getHeldItemSlot();
        if (heldItemSlot == hasArtifact - 9) {
            if (event.isSneaking()) {
                // Игрок начал приседать
                float pitch = player.getLocation().getPitch();
                sneakingPlayers.put(playerId, pitch);
            } else {
                // Игрок прекратил приседать
                Float startPitch = sneakingPlayers.remove(playerId);
                if (startPitch != null) {
                    float endPitch = player.getLocation().getPitch();
                    float pitchDifference = endPitch - startPitch;

                    if (pitchDifference > 3) {
                        if (getPlayerOneSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerOneSphere(player.getUniqueId(), "G");
                        } else if (getPlayerTwoSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerTwoSphere(player.getUniqueId(), "G");
                        } else if (getPlayerThreeSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerThreeSphere(player.getUniqueId(), "G");
                        } else {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerOneSphere(player.getUniqueId(), "G");
                            setPlayerTwoSphere(player.getUniqueId(), "-");
                            setPlayerThreeSphere(player.getUniqueId(), "-");
                        }
                    } else if (pitchDifference < -3) {
                        if (getPlayerOneSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerOneSphere(player.getUniqueId(), "A");
                        } else if (getPlayerTwoSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerTwoSphere(player.getUniqueId(), "A");
                        } else if (getPlayerThreeSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerThreeSphere(player.getUniqueId(), "A");
                        } else {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerOneSphere(player.getUniqueId(), "A");
                            setPlayerTwoSphere(player.getUniqueId(), "-");
                            setPlayerThreeSphere(player.getUniqueId(), "-");
                        }
                    } else { // Камера осталась в нейтральном положении
                        if (getPlayerOneSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerOneSphere(player.getUniqueId(), "B");
                        } else if (getPlayerTwoSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerTwoSphere(player.getUniqueId(), "B");
                        } else if (getPlayerThreeSphere(player.getUniqueId()) == "-") {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerThreeSphere(player.getUniqueId(), "B");
                        } else {
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_GLOW_SQUID_AMBIENT, 0.7f, 1.0f, 10);
                            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 0.3f, 1.0f, 10);
                            setPlayerOneSphere(player.getUniqueId(), "B");
                            setPlayerTwoSphere(player.getUniqueId(), "-");
                            setPlayerThreeSphere(player.getUniqueId(), "-");
                        }
                    }
                }
            }
        }
    }

    private void handleSpellCaster(Player player) {
        // Get spheres
        String oneSphere = getPlayerOneSphere(player.getUniqueId());
        String twoSphere = getPlayerTwoSphere(player.getUniqueId());
        String threeSphere = getPlayerThreeSphere(player.getUniqueId());

        if (oneSphere.equals("A") && twoSphere.equals("A") && threeSphere.equals("A")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 8, 1, false, false));
        } else if (oneSphere.equals("A") && twoSphere.equals("A") && threeSphere.equals("B") || oneSphere.equals("A") && twoSphere.equals("B") && threeSphere.equals("A") || oneSphere.equals("B") && twoSphere.equals("A") && threeSphere.equals("A")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 8, 0, false, false));
        } else if (oneSphere.equals("A") && twoSphere.equals("A") && threeSphere.equals("G") || oneSphere.equals("A") && twoSphere.equals("G") && threeSphere.equals("A") || oneSphere.equals("G") && twoSphere.equals("A") && threeSphere.equals("A")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 8, 0, false, false));
        } else if (oneSphere.equals("A") && twoSphere.equals("B") && threeSphere.equals("B") || oneSphere.equals("B") && twoSphere.equals("A") && threeSphere.equals("B") || oneSphere.equals("B") && twoSphere.equals("B") && threeSphere.equals("A")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 8, 1, false, false));
        } else if (oneSphere.equals("A") && twoSphere.equals("G") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("A") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("G") && threeSphere.equals("A")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 8, 3, false, false));
        } else if (oneSphere.equals("B") && twoSphere.equals("B") && threeSphere.equals("B")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 8, 1, false, false));
        } else if (oneSphere.equals("B") && twoSphere.equals("B") && threeSphere.equals("G") || oneSphere.equals("B") && twoSphere.equals("G") && threeSphere.equals("B") || oneSphere.equals("G") && twoSphere.equals("B") && threeSphere.equals("B")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 8, 0, false, false));
        } else if (oneSphere.equals("B") && twoSphere.equals("G") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("B") && threeSphere.equals("G") || oneSphere.equals("G") && twoSphere.equals("G") && threeSphere.equals("B")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 8, 0, false, false));
        } else if (oneSphere.equals("G") && twoSphere.equals("G") && threeSphere.equals("G")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 0, false, false));
        } else if (oneSphere.equals("A") && twoSphere.equals("B") && threeSphere.equals("G") || oneSphere.equals("A") && twoSphere.equals("G") && threeSphere.equals("B") || oneSphere.equals("B") && twoSphere.equals("A") && threeSphere.equals("G") || oneSphere.equals("B") && twoSphere.equals("G") && threeSphere.equals("A") || oneSphere.equals("G") && twoSphere.equals("A") && threeSphere.equals("B") || oneSphere.equals("G") && twoSphere.equals("B") && threeSphere.equals("A")) {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.0f, 10);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 8, 0));
        } else {
            SoundUtil.playSoundForNearbyPlayers(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.3f, 1.0f, 10);
        }
    }
}