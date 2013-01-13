package com.kokakiwi.mclauncher.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.ui.simple.SimpleTheme;
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
			String yml = executePost("http://update.brautec.de/profiles.php?dl=true&game="+id, "", "",true).replaceAll("[^\\x00-\\x7F]", "");
			String v = executePost("http://update.brautec.de/profiles.php?game="+id, "", "",false);
			this.writeFile(version,v);
	        this.writeFile(file,yml);
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
    		ProfileVersion = Integer.parseInt(executePost("http://update.brautec.de/profiles.php?game="+game, "", "",false));
    	} catch (Exception e) {
    		MCLogger.debug("Error on Request of: "+"http://update.brautec.de/profiles.php?game="+game);
    		this.noask = true;
		}
    	return ProfileVersion;
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
	
	public static String executePost(String targetURL, String urlParameters,
            String keyFileName,boolean multiline)
    {
        final String protocol = targetURL.substring(4);
        HttpURLConnection connection = null;
        try
        {
            final URL url = new URL(targetURL);
            if (protocol.contains("https"))
            {
                connection = (HttpsURLConnection) url.openConnection();
            }
            else
            {
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            
            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            
            connection.connect();
            
            if (protocol.contains("https"))
            {
                final Certificate[] certs = ((HttpsURLConnection) connection)
                        .getServerCertificates();
                
                final byte[] bytes = new byte[294];
                final DataInputStream dis = new DataInputStream(
                        getResourceAsStream("keys/" + keyFileName));
                dis.readFully(bytes);
                dis.close();
                
                final Certificate c = certs[0];
                final PublicKey pk = c.getPublicKey();
                final byte[] data = pk.getEncoded();
                
                for (int i = 0; i < data.length; i++)
                {
                    if (data[i] == bytes[i])
                    {
                        continue;
                    }
                    throw new RuntimeException("Public key mismatch");
                }
            }
            
            final DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            
            final InputStream is = connection.getInputStream();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(
                    is));
            
            final StringBuffer response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null)
            {
                response.append(line);
                if(multiline == true)
                	response.append("\r\n");
                	
            }
            rd.close();
            
            final String str1 = response.toString();
            return str1;
        }
        catch (final Exception e)
        {
            MCLogger.debug(e.getLocalizedMessage());
            return null;
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }
	
	public static InputStream getResourceAsStream(String url)
    {
        return SimpleTheme.class.getResourceAsStream("/"+url);
    }
	
    public Integer readVersion(File file) throws Exception
    {
    	String str = "0";
		FileInputStream stream = new FileInputStream(file);
		try {
		  FileChannel fc = stream.getChannel();
		  MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		  str = Charset.defaultCharset().decode(bb).toString();
		}
		finally {
		  stream.close();
		}
		
        return Integer.parseInt(str);
    }
    
    public void writeFile(File file, String text) throws Exception
    {
        if (!file.exists())
        {
            file.createNewFile();
        }
        final DataOutputStream dos = new DataOutputStream(new FileOutputStream(
                file));
        dos.write(text.getBytes());
        dos.close();
    }
}
