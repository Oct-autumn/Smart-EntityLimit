package cn.OctAutumn.SmartEntityLimit;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class Core extends JavaPlugin
{
    FileConfiguration MainConfig = null;
    //boolean EnableFlag = true;

    int Interval_time = 600;
    float MinTPS = 15.00f;
    int TPS_KeepTime = 30;
    boolean SmartMode_Enabled = true;
    boolean NoneAI_Enabled = false;

    public void onEnable()
    {
        saveDefaultConfig();
        getLogger().info("---Built by OctAutumn---");
        getLogger().info("Loading config file...");
        MainConfig = getConfig();
        if (MainConfig == null)
        {
            getLogger().info("\033[31;1m" + "[ERROR] Load Main config file FAIL." + "\033[0m");
            getLogger().info("\033[33;1m" + "[WARNING] Plugin will use Default config." + "\033[0m");
        }
        else
        {
            getLogger().info("\033[32;1m" + "[INFO] Load Main config file SUCCEED." + "\033[0m");
            Interval_time = MainConfig.getInt("Interval time");
            MinTPS = MainConfig.getObject("MinTPS", float.class);
            TPS_KeepTime = MainConfig.getInt("Keep time");
            SmartMode_Enabled = MainConfig.getBoolean("Enable SMART mode");
            NoneAI_Enabled = MainConfig.getBoolean("Enable NoneAI");

            getLogger().info("[INFO] Check interval time is: " + Interval_time);
            getLogger().info("[INFO] TPS limit is: " + MinTPS);
            getLogger().info("[INFO] TPS keep time: "+ TPS_KeepTime);
            getLogger().info("[INFO] SMART mode ");
            if (SmartMode_Enabled)
                getLogger().info("\033[31;1m"+"Disabled"+"\033[0m");
            else
                getLogger().info("\033[32;1m"+"Enabled"+"\033[0m");
            getLogger().info("[INFO] NoneAI ");
            if (NoneAI_Enabled)
                getLogger().info("\033[31;1m"+"Disabled"+"\033[0m");
            else
                getLogger().info("\033[32;1m"+"Enabled"+"\033[0m");
        }
    }
}
