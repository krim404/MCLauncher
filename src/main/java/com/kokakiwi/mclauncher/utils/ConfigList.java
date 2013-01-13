package com.kokakiwi.mclauncher.utils;

import java.io.File;
import java.util.HashMap;

import com.kokakiwi.mclauncher.api.LauncherAPI;

public class ConfigList 
{
	LauncherAPI api;
	HashMap<String,Configuration> cfgs = new HashMap<String,Configuration>();
	String profilesParentDir = System.getProperty("user.home", ".") + "/"+ "bdh2/";
  	
	public ConfigList(LauncherAPI api)
	{
		this.api = api;
		this.updateConfigs();
		this.reloadConfigs();
	}
	
	public void updateConfigs()
	{
		final File profilesDir = new File(profilesParentDir + "profiles");
		if(!profilesDir.exists())
		{
			profilesDir.mkdirs();
		}
		
        final String[] profDir = profilesDir.list();
        for (final String dir : profDir)
        {
            final File profileDir = new File(profilesDir, dir);
            if (profileDir.isDirectory())
            {
            	//TODO: Version prüfen, gegen Masterserver abfragen ob neue Version verfügbar, wenn ja: registerConfig();
            }
        }
	}
	
	//Abfrage gegen Master nach ID, anlegen des Ordners und speichern der Config File. Danach reload
	public boolean registerConfig(String id)
	{
		//TODO
		return true;
	}
	
	public void reloadConfigs()
	{
		this.cfgs = new HashMap<String,Configuration>();
		this.cfgs.put("Default", Configuration.getLauncherConfiguration());
		
		final File profilesDir = new File(profilesParentDir + "profiles");
        final String[] profDir = profilesDir.list();
        for (final String dir : profDir)
        {
            final File profileDir = new File(profilesDir, dir);
            if (profileDir.isDirectory())
            {
            	MCLogger.debug("Load profile with ID '" + dir + "'");
                final File descriptorFile = new File(profileDir, "profile.yml");
                if (descriptorFile.exists())
                {
                    this.loadConfig(descriptorFile);
                }
            }
        }
	}
	
	public boolean loadConfig(File f)
	{
		Configuration c = new Configuration();
		if(c.load(f))
		{	
			this.cfgs.put(c.getString("game.id"),c);
			return true;
		} else return false;
	}

	
	public Configuration getConfig(String title)
	{
		return this.cfgs.get(title);
	}
	
	public HashMap<String,Configuration> getConfigs()
	{
		return this.cfgs;
	}
}
