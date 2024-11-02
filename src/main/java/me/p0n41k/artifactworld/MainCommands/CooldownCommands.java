package me.p0n41k.artifactworld.MainCommands;

import me.p0n41k.artifactworld.power.*;
import me.p0n41k.artifactworld.power.AdminsPower.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CooldownCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Используйте: /cdreset <Player>");
            return true;
        }

        Player targetPlayer = sender.getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage("Игрок не в сети");
            return true;
        }

        resetAllCooldowns(targetPlayer);

        return true;
    }

    public static void resetAllCooldowns(Player targetPlayer) {
        MagicFireOne.resetAllCooldownsMagicFireOne(targetPlayer);
        SpellJerk.resetAllCooldownsSpellJerk(targetPlayer);
        LightningSpeed.resetAllCooldownsLightningSpeed(targetPlayer);
        Repulsion.resetAllCooldownsRepulsion(targetPlayer);
        Attraction.resetAllCooldownsAttraction(targetPlayer);
        Trap.resetAllCooldownsTrap(targetPlayer);
        Justice.resetAllCooldownsJustice(targetPlayer);
        Beacon.resetAllCooldownsBeacon(targetPlayer);
        RandomPortal.resetAllCooldownsRandomPortal(targetPlayer);
        AttractiveFlow.resetAllCooldownsAttractiveFlow(targetPlayer);
        Transformation.resetAllCooldownsTransformation(targetPlayer);
        AbsorptionShield.resetAllCooldownsAbsorptionShield(targetPlayer);
        SummonUndead.resetAllCooldownsSummonUndead(targetPlayer);
        GhostlyForm.resetAllCooldownsGhostlyForm(targetPlayer);
        Spellcaster.resetAllCooldownsSpellCaster(targetPlayer);
        Deflection.resetAllCooldownsDeflection(targetPlayer);
        AntiMagicArea.resetAllCooldownsAntiMagicArea(targetPlayer);
        AstralStep.resetAllCooldownsAstralStep(targetPlayer);
        BlindFury.resetAllCooldownsBlindFury(targetPlayer);
        PoisonDart.resetAllCooldownsPoisonDart(targetPlayer);
        Rollback.resetAllCooldownsRollback(targetPlayer);
        EMP.resetAllCooldownsEMP(targetPlayer);
        TemperatureDrop.resetAllCooldownsTemperatureDrop(targetPlayer);
        CurseOfDarkness.resetAllCooldownsCurseOfDarkness(targetPlayer);
        Boomerang.resetAllCooldownsBoomerang(targetPlayer);


        Bubble.resetAllCooldownsBubble(targetPlayer);

        RequiemOfSouls.resetAllCooldownsRequiemOfSouls(targetPlayer);
        Coil1.resetAllCooldownsCoil1(targetPlayer);
        Coil2.resetAllCooldownsCoil2(targetPlayer);
        Coil3.resetAllCooldownsCoil3(targetPlayer);

        Rearm.resetAllCooldownsRearm(targetPlayer);
        Laser.resetAllCooldownsLaser(targetPlayer);
        MarchOfTheMachines.resetAllCooldownsMarch(targetPlayer);
        KeenConveyance.resetAllCooldownsKeenConveyance(targetPlayer);

        Gender.resetAllCooldownsSex(targetPlayer);
        EchoSlam.resetAllCooldownsEchoSlam(targetPlayer);
        Spit.resetAllCooldownsSpit(targetPlayer);
        KickBarbarian.resetAllCooldownsKickBarbarian(targetPlayer);
    }
}
