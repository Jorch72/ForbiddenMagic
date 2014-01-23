package com.spiteful.forbidden.items;

import com.spiteful.forbidden.Config;
import com.spiteful.forbidden.Compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.IWandRodOnUpdate;
import thaumcraft.common.items.wands.ItemWandCasting;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
//import WayofTime.alchemicalWizardry.common.EnergyItems;

public class BloodWandUpdate implements IWandRodOnUpdate {

	Aspect primals[] = Aspect.getPrimalAspects().toArray(new Aspect[0]);
	Method checkOwner;
	Method syphon;

	public BloodWandUpdate()
	{
		try
		{
			Class energyItems;
			try
			{
				energyItems = Class.forName("WayofTime.alchemicalWizardry.EnergyItems");
			}
			catch(Exception e)
			{
				energyItems = Class.forName("WayofTime.alchemicalWizardry.common.itemsEnergyItems");
			}
			if(energyItems == null)
				return;
			checkOwner = energyItems.getDeclaredMethod("checkAndSetItemOwner", new Class[] {ItemStack.class, EntityPlayer.class});
			syphon = energyItems.getDeclaredMethod("syphonBatteries", new Class[] {ItemStack.class, EntityPlayer.class, Integer.TYPE});
		}
		catch(Exception e){}
	}
	
	public void onUpdate(ItemStack itemstack, EntityPlayer player)
	{
		if(Compat.bm && player.ticksExisted % 80 == 0 && player.getCurrentEquippedItem() == itemstack)
		{
			try
			{
				checkOwner.invoke(null, itemstack, player);
				
				for(int x = 0;x < primals.length;x++)
				{
					if(((ItemWandCasting)itemstack.getItem()).getVis(itemstack, primals[x]) < ((ItemWandCasting)itemstack.getItem()).getMaxVis(itemstack))
					{
						if(((Boolean)(syphon.invoke(null, itemstack, player, 100))).booleanValue())
							((ItemWandCasting)itemstack.getItem()).addVis(itemstack, primals[x], 1, true);
						else
							return;
					}
				}
			
			}
			catch(Exception e)
			{
				return;
			}
		}
		
	}
}