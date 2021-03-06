package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.utils.JsonItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockLootConfiguration {

	private Material material;
	private ItemStack loot;
	private int addXp;

	private List<Material> miningTools = new ArrayList<>();
	
	public BlockLootConfiguration() {
		this.material = Material.AIR;
		this.loot = new ItemStack(material);
		this.addXp = 0;
	}
	
	public boolean parseConfiguration(ConfigurationSection section){
		if (section == null){
			return false;
		}

		try{
			material = Material.valueOf(section.getName());
		}catch(IllegalArgumentException e){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in block-loot. This is not an existing block type. Ignoring it.");
			return false;
		}
		
		String itemStr = section.getString("loot");

		if (itemStr == null){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse section '"+section.getName()+"' in block-loot. Missing loot item.");
			return false;
		}

		try {
			loot = JsonItemUtils.getItemFromJson(itemStr);
		}catch (ParseException ex){
			Bukkit.getLogger().warning("[UhcCore] Couldn't parse loot '"+material.toString()+"' in block-loot.");
			ex.printStackTrace();
			return false;
		}

		List<String> miningToolNames = section.getStringList("mining-tools");
		for (String miningToolName : miningToolNames) {
			Material miningTool = Material.matchMaterial(miningToolName);
			if (miningTool != null) this.miningTools.add(miningTool);
		}

		addXp = section.getInt("add-xp",0);
		return true;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public ItemStack getLoot() {
		return loot;
	}
	
	public int getAddXp() {
		return addXp;
	}

	public List<Material> getMiningTools() {
		return miningTools;
	}

}