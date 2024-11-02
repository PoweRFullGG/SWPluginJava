package me.p0n41k.artifactworld.power.AdminsPower.Commands;

import me.p0n41k.artifactworld.power.*;
import me.p0n41k.artifactworld.power.AdminsPower.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AllSpellsCommand implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    public AllSpellsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только люди могут использовать команду!");
            return true;
        }

        openMainMenu((Player) sender);
        return true;
    }
    private void openInventory(Player player, String title, int size) {
        Inventory inventory = plugin.getServer().createInventory(player, size, title);
        player.openInventory(inventory);
    }

    private void openGodMenu(Player player) {
        openInventory(player, "Админские заклинания", 54);

        ItemStack BubbleItem = Bubble.createBubbleItem();
        ItemStack RequiemOfSoulsItem = RequiemOfSouls.createRequiemOfSoulsItem();
        ItemStack Coil1Item = Coil1.createCoil1Item();
        ItemStack Coil2Item = Coil2.createCoil2Item();
        ItemStack Coil3Item = Coil3.createCoil3Item();
        ItemStack RearmItem = Rearm.createRearmItem();
        ItemStack LaserItem = Laser.createLaserItem();
        ItemStack MarchOfTheMachinesItem = MarchOfTheMachines.createMarchOfTheMachinesItem();
        ItemStack KeenConveyanceItem = KeenConveyance.createKeenConveyanceItem();
        ItemStack GenderItem = Gender.createGenderItem();
        ItemStack EchoSlamItem = EchoSlam.createEchoSlamItem();
        ItemStack SpitItem = Spit.createSpitItem();
        ItemStack KickBarbarianItem = KickBarbarian.createKickBarbarianItem();


        ItemStack menu = new ItemStack(Material.COMPASS);
        ItemMeta menumeta = menu.getItemMeta();
        menumeta.setDisplayName(ChatColor.GREEN + "Menu");
        menu.setItemMeta(menumeta);

        player.getOpenInventory().getTopInventory().setItem(0, BubbleItem);
        player.getOpenInventory().getTopInventory().setItem(1, RequiemOfSoulsItem);
        player.getOpenInventory().getTopInventory().setItem(2, Coil1Item);
        player.getOpenInventory().getTopInventory().setItem(3, Coil2Item);
        player.getOpenInventory().getTopInventory().setItem(4, Coil3Item);
        player.getOpenInventory().getTopInventory().setItem(5, RearmItem);
        player.getOpenInventory().getTopInventory().setItem(6, LaserItem);
        player.getOpenInventory().getTopInventory().setItem(7, MarchOfTheMachinesItem);
        player.getOpenInventory().getTopInventory().setItem(8, KeenConveyanceItem);
        player.getOpenInventory().getTopInventory().setItem(9, GenderItem);
        player.getOpenInventory().getTopInventory().setItem(10, EchoSlamItem);
        player.getOpenInventory().getTopInventory().setItem(11, SpitItem);
        player.getOpenInventory().getTopInventory().setItem(12, KickBarbarianItem);
        player.getOpenInventory().getTopInventory().setItem(49, menu);
    }


    private void openMainMenu(Player player) {
        openInventory(player, "Главное меню", 54);

        ItemStack spellJerkItem = SpellJerk.createSpellJerkItem();
        ItemStack lightningSpeedItem = LightningSpeed.createLightningSpeedItem();
        ItemStack repulsionItem = Repulsion.createRepulsionItem();
        ItemStack attractionItem = Attraction.createAttractionItem();
        ItemStack trapItem = Trap.createTrapItem();
        ItemStack JusticeItem = Justice.createJusticeItem();
        ItemStack BeaconItem = Beacon.createBeaconItem();
        ItemStack RandomPortalItem = RandomPortal.createRandomPortalItem();
        ItemStack AttractiveFlowItem = AttractiveFlow.createAttractiveFlowItem();
        ItemStack TransformationItem = Transformation.createTransformationItem();
        ItemStack AbsorptionShieldItem = AbsorptionShield.createAbsorptionShieldItem();
        ItemStack SummonUndeadItem = SummonUndead.createSummonUndeadItem();
        ItemStack GhostlyFormItem = GhostlyForm.createGhostlyFormItem();
        ItemStack SpellCasterItem = Spellcaster.createSpellCasterItem();
        ItemStack DeflectionItem = Deflection.createDeflectionItem();
        ItemStack AntiMagicAreaItem = AntiMagicArea.createAntiMagicAreaItem();
        ItemStack AstralStepItem = AstralStep.createAstralStepItem();
        ItemStack BlindFuryItem = BlindFury.createBlindFuryItem();
        ItemStack PoisonDartItem = PoisonDart.createPoisonDartItem();
        ItemStack RollbackItem = Rollback.createRollbackItem();
        ItemStack EMPItem = EMP.createEMPItem();
        ItemStack TemperatureDropItem = TemperatureDrop.createTemperatureDropItem();
        ItemStack CurseOfDarknessItem = CurseOfDarkness.createCurseOfDarknessItem();
        ItemStack BoomerangItem = Boomerang.createBoomerangItem();

        ItemStack menuadm = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta admmeta= menuadm.getItemMeta();
        admmeta.setDisplayName(ChatColor.GREEN + "Админские заклинания");
        menuadm.setItemMeta(admmeta);


        player.getOpenInventory().getTopInventory().setItem(0, spellJerkItem);
        player.getOpenInventory().getTopInventory().setItem(1, lightningSpeedItem);
        player.getOpenInventory().getTopInventory().setItem(2, repulsionItem);
        player.getOpenInventory().getTopInventory().setItem(3, attractionItem);
        player.getOpenInventory().getTopInventory().setItem(4, trapItem);
        player.getOpenInventory().getTopInventory().setItem(5, JusticeItem);
        player.getOpenInventory().getTopInventory().setItem(6, BeaconItem);
        player.getOpenInventory().getTopInventory().setItem(7, RandomPortalItem);
        player.getOpenInventory().getTopInventory().setItem(8, AttractiveFlowItem);
        player.getOpenInventory().getTopInventory().setItem(9, TransformationItem);
        player.getOpenInventory().getTopInventory().setItem(10, AbsorptionShieldItem);
        player.getOpenInventory().getTopInventory().setItem(11, SummonUndeadItem);
        player.getOpenInventory().getTopInventory().setItem(12, GhostlyFormItem);
        player.getOpenInventory().getTopInventory().setItem(13, SpellCasterItem);
        player.getOpenInventory().getTopInventory().setItem(14, DeflectionItem);
        player.getOpenInventory().getTopInventory().setItem(15, AntiMagicAreaItem);
        player.getOpenInventory().getTopInventory().setItem(16, AstralStepItem);
        player.getOpenInventory().getTopInventory().setItem(17, BlindFuryItem);
        player.getOpenInventory().getTopInventory().setItem(18, PoisonDartItem);
        player.getOpenInventory().getTopInventory().setItem(19, RollbackItem);
        player.getOpenInventory().getTopInventory().setItem(20, EMPItem);
        player.getOpenInventory().getTopInventory().setItem(21, TemperatureDropItem);
        player.getOpenInventory().getTopInventory().setItem(22, CurseOfDarknessItem);
        player.getOpenInventory().getTopInventory().setItem(23, BoomerangItem);
        player.getOpenInventory().getTopInventory().setItem(49, menuadm);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals("Главное меню")) {
            event.setCancelled(true);

            if (event.getRawSlot() == 0) {
                ItemStack spellJerkItem = SpellJerk.createSpellJerkItem();
                player.getInventory().addItem(spellJerkItem);
            } else if (event.getRawSlot() == 1) {
                ItemStack lightningSpeedItem = LightningSpeed.createLightningSpeedItem();
                player.getInventory().addItem(lightningSpeedItem);
            } else if (event.getRawSlot() == 2) {
                ItemStack repulsionItem = Repulsion.createRepulsionItem();
                player.getInventory().addItem(repulsionItem);
            } else if (event.getRawSlot() == 3) {
                ItemStack attractionItem = Attraction.createAttractionItem();
                player.getInventory().addItem(attractionItem);
            } else if (event.getRawSlot() == 4) {
                ItemStack trapItem = Trap.createTrapItem();
                player.getInventory().addItem(trapItem);
            } else if (event.getRawSlot() == 5) {
                ItemStack JusticeItem = Justice.createJusticeItem();
                player.getInventory().addItem(JusticeItem);
            } else if (event.getRawSlot() == 6) {
                ItemStack BeaconItem = Beacon.createBeaconItem();
                player.getInventory().addItem(BeaconItem);
            } else if (event.getRawSlot() == 7) {
                ItemStack RandomPortalItem = RandomPortal.createRandomPortalItem();
                player.getInventory().addItem(RandomPortalItem);
            } else if (event.getRawSlot() == 8) {
                ItemStack AttractiveFlowItem = AttractiveFlow.createAttractiveFlowItem();
                player.getInventory().addItem(AttractiveFlowItem);
            } else if (event.getRawSlot() == 9) {
                ItemStack TransformationItem = Transformation.createTransformationItem();
                player.getInventory().addItem(TransformationItem);
            } else if (event.getRawSlot() == 10) {
                ItemStack AbsorptionShieldItem = AbsorptionShield.createAbsorptionShieldItem();
                player.getInventory().addItem(AbsorptionShieldItem);
            } else if (event.getRawSlot() == 11) {
                ItemStack SummonUndeadItem = SummonUndead.createSummonUndeadItem();
                player.getInventory().addItem(SummonUndeadItem);
            } else if (event.getRawSlot() == 12) {
                ItemStack GhostlyFormItem = GhostlyForm.createGhostlyFormItem();
                player.getInventory().addItem(GhostlyFormItem);
            } else if (event.getRawSlot() == 13) {
                ItemStack SpellCasterItem = Spellcaster.createSpellCasterItem();
                player.getInventory().addItem(SpellCasterItem);
            } else if (event.getRawSlot() == 14) {
                ItemStack DeflectionItem = Deflection.createDeflectionItem();
                player.getInventory().addItem(DeflectionItem);
            } else if (event.getRawSlot() == 15) {
                ItemStack AntiMagicAreaItem = AntiMagicArea.createAntiMagicAreaItem();
                player.getInventory().addItem(AntiMagicAreaItem);
            } else if (event.getRawSlot() == 16) {
                ItemStack AstralStepItem = AstralStep.createAstralStepItem();
                player.getInventory().addItem(AstralStepItem);
            } else if (event.getRawSlot() == 17) {
                ItemStack BlindFuryItem = BlindFury.createBlindFuryItem();
                player.getInventory().addItem(BlindFuryItem);
            } else if (event.getRawSlot() == 18) {
                ItemStack PoisonDartItem = PoisonDart.createPoisonDartItem();
                player.getInventory().addItem(PoisonDartItem);
            } else if (event.getRawSlot() == 19) {
                ItemStack RollbackItem = Rollback.createRollbackItem();
                player.getInventory().addItem(RollbackItem);
            } else if (event.getRawSlot() == 20) {
                ItemStack EMPItem = EMP.createEMPItem();
                player.getInventory().addItem(EMPItem);
            } else if (event.getRawSlot() == 21) {
                ItemStack TemperatureDropItem = TemperatureDrop.createTemperatureDropItem();
                player.getInventory().addItem(TemperatureDropItem);
            } else if (event.getRawSlot() == 22) {
                ItemStack CurseOfDarknessItem = CurseOfDarkness.createCurseOfDarknessItem();
                player.getInventory().addItem(CurseOfDarknessItem);
            } else if (event.getRawSlot() == 23) {
                ItemStack BoomerangItem = Boomerang.createBoomerangItem();
                player.getInventory().addItem(BoomerangItem);
            } else if (event.getRawSlot() == 49) {
                openGodMenu(player);
            }
        }
        if (title.equals("Админские заклинания")) {
            event.setCancelled(true);

            if (event.getRawSlot() == 0) {
                ItemStack BubbleItem = Bubble.createBubbleItem();
                player.getInventory().addItem(BubbleItem);
            } else if (event.getRawSlot() == 1) {
                ItemStack RequiemOfSoulsItem = RequiemOfSouls.createRequiemOfSoulsItem();
                player.getInventory().addItem(RequiemOfSoulsItem);
            } else if (event.getRawSlot() == 2) {
                ItemStack Coil1Item = Coil1.createCoil1Item();
                player.getInventory().addItem(Coil1Item);
            } else if (event.getRawSlot() == 3) {
                ItemStack Coil2Item = Coil2.createCoil2Item();
                player.getInventory().addItem(Coil2Item);
            } else if (event.getRawSlot() == 4) {
                ItemStack Coil3Item = Coil3.createCoil3Item();
                player.getInventory().addItem(Coil3Item);
            } else if (event.getRawSlot() == 5) {
                ItemStack RearmItem = Rearm.createRearmItem();
                player.getInventory().addItem(RearmItem);
            } else if (event.getRawSlot() == 6) {
                ItemStack LaserItem = Laser.createLaserItem();
                player.getInventory().addItem(LaserItem);
            } else if (event.getRawSlot() == 7) {
                ItemStack MarchOfTheMachinesItem = MarchOfTheMachines.createMarchOfTheMachinesItem();
                player.getInventory().addItem(MarchOfTheMachinesItem);
            } else if (event.getRawSlot() == 8) {
                ItemStack KeenConveyanceItem = KeenConveyance.createKeenConveyanceItem();
                player.getInventory().addItem(KeenConveyanceItem);
            } else if (event.getRawSlot() == 9) {
                ItemStack GenderItem = Gender.createGenderItem();
                player.getInventory().addItem(GenderItem);
            } else if (event.getRawSlot() == 10) {
                ItemStack EchoSlamItem = EchoSlam.createEchoSlamItem();
                player.getInventory().addItem(EchoSlamItem);
            } else if (event.getRawSlot() == 11) {
                ItemStack SpitItem = Spit.createSpitItem();
                player.getInventory().addItem(SpitItem);
            } else if (event.getRawSlot() == 12) {
                ItemStack KickBarbarianItem = KickBarbarian.createKickBarbarianItem();
                player.getInventory().addItem(KickBarbarianItem);
            } else if (event.getRawSlot() == 49) {
                openMainMenu(player);
            }
        }
    }
}
