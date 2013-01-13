package com.kokakiwi.mclauncher.utils;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import com.kokakiwi.mclauncher.api.LauncherAPI;

public class ConfigListModel implements ListModel
{
	public ConfigList c;
    
    public ConfigListModel(LauncherAPI api)
    {
    	this.c = api.getConfigList();
    }
    

    
    public Object getElementAt(int index)
    {
        return c.getConfigs().keySet().toArray()[index];
    }
    
    public int getSize()
    {
    	return c.getConfigs().size();
    }
    
    public void removeListDataListener(ListDataListener l)
    {
        
    }
    
	public void addListDataListener(ListDataListener l) 
	{
		
	}
    
}
