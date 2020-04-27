package net.echoweaver.diamondsforever;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

public class EntityInteractHandler {

  @SubscribeEvent(receiveCanceled = true)
  public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {

    if (event.getEntity() instanceof EntityPlayer        // Player interacts with Villager
        && event.getTarget() instanceof EntityVillager
        && !event.getEntityPlayer().world.isRemote        // WORLD MUST NOT BE CLIENT (TODO: Is this necessary?)
    )
    {
      EntityPlayer player = event.getEntityPlayer();
      ItemStack itemstackMain = player.getHeldItemMainhand();
      EntityVillager target = (EntityVillager) event.getTarget();
      World world = player.world;
      // Nearest village to the target entity
      Village villageNearTarget = world.getVillageCollection().getNearestVillage(
          new BlockPos(target.posX, target.posY, target.posZ), 32);

      if (itemstackMain.getItem() == Items.DIAMOND)
      {
        // We're going to get two of this interaction, one for each hand.
        // Only act on the main hand and ignore the off hand.
        // Both will be canceled at the end to avoid opening the trade window.
        if (event.getHand() == EnumHand.MAIN_HAND) {
          FMLLog.log(DiamondsForever.NAME, Level.INFO, "Trying to mate");
          if (target.getGrowingAge() < 0) {
            if (!world.isRemote) {
              player.sendMessage(new TextComponentString(target.getName()
                  + " is too young for that sort of stuff! Eww!"));
            }
            FMLLog.log(DiamondsForever.NAME, Level.INFO, target.getName()
                + " is too young for that sort of stuff! Eww! (" + target.getGrowingAge() + ")");
            event.setCanceled(true);
          } else if (!target.getIsWillingToMate(true)) {
            if (!world.isRemote) {
              player.sendMessage(new TextComponentString(target.getName() + " has lost that lovin' feeling."));
            }
            FMLLog.log(DiamondsForever.NAME, Level.INFO, target.getName() + " has lost that lovin' feeling.");
            event.setCanceled(true);
        } else if (villageNearTarget.getNumVillageDoors() <= villageNearTarget.getNumVillagers()) {
          if (!world.isRemote) {
            player.sendMessage(new TextComponentString(target.getName()
                + " thinks this place is too crowded to add to the family."));
          }
          FMLLog.log(DiamondsForever.NAME, Level.INFO, target.getName()
              + " thinks this place is too crowded to add to the family. (Doors: "
              + villageNearTarget.getNumVillageDoors() + ", Villagers: " + villageNearTarget.getNumVillagers() + ")");
          event.setCanceled(true);
          } else {
            Entity mate = target.world.findNearestEntityWithinAABB(EntityVillager.class, target.getEntityBoundingBox().grow(8.0D, 3.0D, 8.0D), target);
            if (mate == null || ((EntityVillager) mate).getGrowingAge() < 0) {
              if (!world.isRemote) {
                player.sendMessage(new TextComponentString("Nobody wants to mate with " + target.getName() + ". Aww."));
              }
              FMLLog.log(DiamondsForever.NAME, Level.INFO, "Nobody wants to mate with " + target.getName() + "!");
            } else if (!((EntityVillager) mate).getIsWillingToMate(true)) {
              if (!world.isRemote) {
                player.sendMessage(new TextComponentString(mate.getName() + " turned down a booty call from " + target.getName()
                    + ". Denied!"));
              }
              FMLLog.log(DiamondsForever.NAME, Level.INFO, mate.getName() + " turned down a booty call from " + target.getName()
                  + ". Denied!");
            } else {
              EntityVillager mate_villager = (EntityVillager) mate;
              if (!world.isRemote) {
                player.sendMessage(new TextComponentString(mate_villager.getName() + " is giving "
                    + target.getName() + " a come hither look."));
              }
              FMLLog.log(DiamondsForever.NAME, Level.INFO, mate_villager + " is giving " + target.getName()
                  + " a come hither look.");
              mate_villager.getNavigator().tryMoveToEntityLiving(target, 0.25D);
              mate_villager.setGrowingAge(6000);
              target.setGrowingAge(6000);
              mate_villager.setIsWillingToMate(false);
              target.setIsWillingToMate(false);
              EntityAgeable baby_villager = target.createChild(mate_villager);
              baby_villager.setGrowingAge(-24000);
              baby_villager.setLocationAndAngles(target.posX, target.posY, target.posZ, 0.0F, 0.0F);
              target.world.spawnEntity(baby_villager);
              target.world.setEntityState(baby_villager, (byte) 12);
              if (!world.isRemote) {
                player.sendMessage(new TextComponentString(target.getName() + " and "
                    + mate_villager.getName() + " had a beautiful bouncing baby!"));
              }
              FMLLog.log(DiamondsForever.NAME, Level.INFO, target.getName() + " and "
                  + mate_villager.getName() + " had a beautiful bouncing baby!");
            }
          }
        }
        event.setCanceled(true);
      }
    }
  }
}
