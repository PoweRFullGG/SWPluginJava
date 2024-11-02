package me.p0n41k.artifactworld;

import me.p0n41k.artifactworld.MainCommands.CooldownCommands;
import me.p0n41k.artifactworld.MainCommands.PlayTimeCommand;
import me.p0n41k.artifactworld.MegaTools.Eula;
import me.p0n41k.artifactworld.MegaTools.Knight;
import me.p0n41k.artifactworld.MegaTools.Waka;
import me.p0n41k.artifactworld.RepCommands.RepCommands;
import me.p0n41k.artifactworld.artmenu.ArtifactMenu;
import me.p0n41k.artifactworld.artmenu.ArtifactMaxSlot;
import me.p0n41k.artifactworld.ManaCommands.ManaMaxPlayer;
import me.p0n41k.artifactworld.ManaCommands.SecondManaCommand;
import me.p0n41k.artifactworld.ManaCommands.SecondManaPlus;
import me.p0n41k.artifactworld.artmenu.RunaMenu;
import me.p0n41k.artifactworld.artmenu.SpellResetCommand;
import me.p0n41k.artifactworld.power.*;
import me.p0n41k.artifactworld.power.AdminsPower.*;
import me.p0n41k.artifactworld.power.AdminsPower.Commands.AllSpellsCommand;
import me.p0n41k.artifactworld.power.AdminsPower.Commands.Flash;
import me.p0n41k.artifactworld.power.Mechanics.CraftPaperForSpell;
import me.p0n41k.artifactworld.power.Runa.EvasionRuna;
import me.p0n41k.artifactworld.power.Runa.ManaRuna;
import me.p0n41k.artifactworld.rubins.RubinsCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import me.p0n41k.artifactworld.MegaTools.Katana;
import org.bukkit.entity.Entity;


public final class Artifactworld extends JavaPlugin implements Listener {

    private final ArtifactMenu artifactMenu;
    private final RunaMenu runaMenu;
    private final ArtifactMaxSlot artifactMaxSlot;
    private final ManaMaxPlayer manaMaxPlayer;
    private final SecondManaCommand secondManaCommand;
    private final RubinsCommand rubinsCommand;
    private final RepCommands repCommand;
    private ScoreBoardPlayer scoreBoardPlayer;
    private SecondManaPlus secondManaPlus;
    private final SpellResetCommand spellResetCommand;

    public Artifactworld() {
        this.artifactMenu = new ArtifactMenu(this);
        this.runaMenu = new RunaMenu(this);
        this.artifactMaxSlot = new ArtifactMaxSlot();
        this.manaMaxPlayer = new ManaMaxPlayer();
        this.secondManaCommand = new SecondManaCommand();
        this.rubinsCommand = new RubinsCommand();
        this.repCommand = new RepCommands();
        this.spellResetCommand = new SpellResetCommand(artifactMenu);
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        removeSmallArmorStands();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        });

        Bukkit.getServer().getPluginManager().registerEvents(artifactMenu, this);
        getCommand("spellmenu").setExecutor(artifactMenu);

        Bukkit.getServer().getPluginManager().registerEvents(runaMenu, this);
        getCommand("runamenu").setExecutor(runaMenu);

        // Регистрация команд
        getCommand("setmaxslot").setExecutor(artifactMaxSlot);
        getCommand("addmaxslot").setExecutor(artifactMaxSlot);

        getCommand("setrubins").setExecutor(rubinsCommand);
        getCommand("addrubins").setExecutor(rubinsCommand);

        getCommand("setrep").setExecutor(repCommand);
        getCommand("addrep").setExecutor(repCommand);

        getCommand("setmaxmana").setExecutor(manaMaxPlayer);
        getCommand("addmaxmana").setExecutor(manaMaxPlayer);

        getCommand("setsecondmana").setExecutor(secondManaCommand);

        getCommand("playtime").setExecutor(new PlayTimeCommand());

        getCommand("cdreset").setExecutor(new CooldownCommands());

        getCommand("resetspells").setExecutor(spellResetCommand);

        getCommand("flash").setExecutor(new Flash(this));

        getCommand("allspells").setExecutor(new AllSpellsCommand(this));

        getServer().getPluginManager().registerEvents(new NoUseEnderPearl(), this);

        Bukkit.getServer().getPluginManager().registerEvents(new NoBreak(), this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        // АДМИНСКИЕ СПОСОБНОСТИ  (СВИТКИ):

        Bubble bubble = new Bubble(this);

        RequiemOfSouls requiemOfSouls = new RequiemOfSouls(this);

        Coil1 coil1 = new Coil1(this);
        Coil2 coil2 = new Coil2(this);
        Coil3 coil3 = new Coil3(this);

        Rearm rearm = new Rearm(this);
        Laser laser = new Laser(this);
        MarchOfTheMachines marchOfTheMachines = new MarchOfTheMachines(this);
        KeenConveyance keenConveyance = new KeenConveyance(this);

        Gender gender = new Gender(this);
        EchoSlam echoSlam = new EchoSlam(this);
        Spit spit = new Spit(this);
        KickBarbarian kickBarbarian = new KickBarbarian(this);

        //СПОСОБНОСТИ (СВИТКИ):

        MagicFireOne magicfireone = new MagicFireOne(this);

        SpellJerk spelljerk = new SpellJerk(this);

        LightningSpeed lightningspeed = new LightningSpeed(this);

        Repulsion repulsion = new Repulsion(this);

        Attraction attraction = new Attraction(this);

        Trap trap = new Trap(this);

        Justice justice = new Justice(this);

        Beacon beacon = new Beacon(this);

        RandomPortal randomportal = new RandomPortal(this);

        AttractiveFlow attractiveFlow = new AttractiveFlow(this);

        Transformation transformation = new Transformation(this);

        AbsorptionShield absorptionShield = new AbsorptionShield(this);

        SummonUndead summonUndead = new SummonUndead(this);

        GhostlyForm ghostlyForm = new GhostlyForm(this);

        Spellcaster spellcaster = new Spellcaster(this);

        Deflection deflection = new Deflection(this);

        AntiMagicArea antiMagicArea = new AntiMagicArea(this);

        AstralStep astralStep = new AstralStep(this);

        BlindFury blindFury = new BlindFury(this);

        PoisonDart poisonDart = new PoisonDart(this);

        Rollback rollback = new Rollback(this);

        EMP emp = new EMP(this);

        TemperatureDrop temperatureDrop = new TemperatureDrop(this);

        CurseOfDarkness curseOfDarkness = new CurseOfDarkness(this);

        Boomerang boomerang = new Boomerang(this);


        //ПАССИВКИ (РУНЫ):

        ManaRuna manaRuna = new ManaRuna(this);
        getServer().getPluginManager().registerEvents(manaRuna, this);


        EvasionRuna evasionRuna = new EvasionRuna(this);
        getServer().getPluginManager().registerEvents(evasionRuna, this);

        //ДРУГОЕ:

        this.scoreBoardPlayer = new ScoreBoardPlayer(this);
        this.secondManaPlus = new SecondManaPlus(this);

        //Оружие

        getServer().getPluginManager().registerEvents(new Katana(this), this);

        getServer().getPluginManager().registerEvents(new Eula(this), this);

        getServer().getPluginManager().registerEvents(new Waka(this), this);

        getServer().getPluginManager().registerEvents(new Knight(this), this);


        // Регистрация обработчика событий
        getServer().getPluginManager().registerEvents(new NoElytra(), this);
        getServer().getPluginManager().registerEvents(new NoUseEnchantingTable(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new NoUseHorseArmorWithCustomModelData(), this);


        new CraftPaperForSpell(this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            artifactMenu.saveartmenu(player);
            runaMenu.saverunamenu(player);
            GhostlyForm.setGhostPlayer(player, false);
            player.setWalkSpeed(0.2f);
            player.setFlySpeed(0.1f);
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getOpenInventory().getTitle().equals(ChatColor.DARK_PURPLE + "Меню заклинаний")) {
                artifactMenu.savePlayerInventory(player);
            }
            if (player.getOpenInventory().getTitle().equals(ChatColor.DARK_PURPLE + "Меню рун")) {
                runaMenu.savePlayerInventoryRuna(player);
            }
            GhostlyForm.setGhostPlayer(player, false);
        });
    }

    private void removeSmallArmorStands() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand) {
                    ArmorStand armorStand = (ArmorStand) entity;
                    if (armorStand.hasMetadata("unmovable")) {
                        armorStand.remove();
                    }
                }
            }
        }
    }
}
