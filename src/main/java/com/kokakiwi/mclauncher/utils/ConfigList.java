package com.kokakiwi.mclauncher.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.ui.simple.UpdateDialog;

public class ConfigList 
{
	Boolean noask = false;
	LauncherAPI api;
	HashMap<String,Configuration> cfgs = new HashMap<String,Configuration>();
	String profilesParentDir;
  	
	public ConfigList(LauncherAPI api)
	{
		this.api = api;
		this.profilesParentDir = api.getLauncherDirectory().toString()+"/";
		this.updateConfigs();
		this.reloadConfigs();
	}
	
	public void updateConfigs()
	{
		final File profilesDir = new File(profilesParentDir + "profiles/");
		System.out.println(profilesDir.toString());
		if(!profilesDir.exists())
		{
			profilesDir.mkdirs();
		}
		
        final String[] profDir = profilesDir.list();
        final ArrayList<String> upd = new ArrayList<String>();
        for (final String dir : profDir)
        {
            final File profileDir = new File(profilesDir, dir);
            if (profileDir.isDirectory())
            {
            	int v = this.getLocalVersion(dir);
            	if(v < this.getVersion(dir))
            	{
            		MCLogger.debug("New Profile Version found for: "+dir);
            		upd.add(dir);
            	}
            }
        }
        
        class UpdateThread implements Runnable
        {
			public UpdateDialog d;
			public UpdateThread(UpdateDialog d)
			{
				this.d = d;
			}
			public void run() 
			{
				d.setVisible(true);
			}
        }
        
        
        if(upd.size() > 0)
        {
        	UpdateDialog d = new UpdateDialog(api, "Profiles", upd.size());
            Thread t = new Thread(new UpdateThread(d));
            t.start();
            
	        for(String up: upd)
	        {
	        	try 
	        	{
					d.setCurr("Loading: " + up);
					this.registerConfig(up);
					d.updateValue();
				} catch (Exception e) {
					
				}
	        }
	        
	        d.setCurr("Done");
	        api.getMain().reload();
        }
        
	}
	
	//Abfrage gegen Master nach ID, anlegen des Ordners und speichern der Config File. Danach reload
	public void registerConfig(String id)
	{
		final File path = new File(profilesParentDir
                + "profiles/"+id);
		final File file = new File(profilesParentDir
                + "profiles/"+id+"/profile.yml");
		final File version = new File(profilesParentDir
                + "profiles/"+id+"/version");
		path.mkdirs();
		
		try {
			String yml = api.executePost("http://update.brautec.de/profiles.php?dl=true&game="+id, "", "",true).replaceAll("[^\\x00-\\x7F]", "");
			String v = api.executePost("http://update.brautec.de/profiles.php?game="+id, "", "",false);
			api.writeFile(version,v);
	        api.writeFile(file,yml);
		} catch (Exception e) {
			MCLogger.debug("Unable to save profile file: "+id);
		}
	}
	
	public int getLocalVersion(String game)
	{
		final File versionFile = new File(profilesParentDir
                + "profiles/"+game+"/version");
    	int curVersion = 0;
    	
    	try 
    	{
	    	if(versionFile.exists())
	    	{
	    		curVersion = readVersion(versionFile);
	    	} 
    	} catch (Exception e) {
    		System.out.println(e);
		}
    	
    	return curVersion;
	}
	
	public int getVersion(String game)
	{
		if(this.noask == true) return 0;
    	int ProfileVersion = 0;
    	
    	try {
    		ProfileVersion = Integer.parseInt(api.executePost("http://update.brautec.de/profiles.php?game="+game, "", "",false));
    	} catch (Exception e) {
    		MCLogger.debug("Error on Request of: "+"http://update.brautec.de/profiles.php?game="+game);
    		this.noask = true;
		}
    	return ProfileVersion;
	}
	
	
	public void reloadConfigs()
	{
		this.cfgs = new HashMap<String,Configuration>();
		this.cfgs.put("default", Configuration.getLauncherConfiguration());
		
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
                    if(!this.loadConfig(descriptorFile))
                    {
                    	this.registerConfig(dir);
                    	this.loadConfig(descriptorFile);
                    }
                } else
                {
                	this.registerConfig(dir);
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
			this.cfgs.put(c.getString("game.id").toLowerCase(),c);
			return true;
		} else return false;
	}

	
	public Configuration getConfig(String title)
	{
		return this.cfgs.get(title.toLowerCase());
	}
	
	public HashMap<String,Configuration> getConfigs()
	{
		return this.cfgs;
	}

    public Integer readVersion(File file) throws Exception
    {
    	String str = api.readFile(file);
        return Integer.parseInt(str);
    }
}
