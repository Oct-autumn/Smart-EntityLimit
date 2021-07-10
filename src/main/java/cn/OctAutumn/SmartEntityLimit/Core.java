package cn.OctAutumn.SmartEntityLimit;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;

public class Core extends JavaPlugin
{
    //主配置文件
    FileConfiguration MainConfig = null;

    //规则文件
    public File Destroy_Entity_File = null;
    public File Keep_Entity_File = null;
    public File RemoveAI_Entity_File = null;
    //规则配置
    public FileConfiguration Destroy_Entity_Config = null;
    public FileConfiguration Keep_Entity_Config = null;
    public FileConfiguration RemoveAI_Entity_Config = null;
    //boolean EnableFlag = true;

    //配置变量 及 默认配置
    public int Interval_time = 600;
    public double MinTPS = 15.00d;
    public int TPS_KeepTime = 30;
    public boolean SmartMode_Enabled = true;
    public boolean NoneAI_Enabled = false;

    //覆写onLoad()方法
    @Override
    public void onLoad()
    {
        saveDefaultConfig();
        getLogger().info("---Built by OctAutumn---");
        //加载主配置文件
        getLogger().info("[INFO] Loading config file...");
        MainConfig = getConfig();
        //反馈加载成功，使用配置文件中的配置
        Interval_time = MainConfig.getInt("Interval time");
        MinTPS = MainConfig.getDouble("MinTPS");
        TPS_KeepTime = MainConfig.getInt("Keep time");
        SmartMode_Enabled = MainConfig.getBoolean("Enable SMART mode");
        NoneAI_Enabled = MainConfig.getBoolean("Enable NoneAI");
        getLogger().info("\033[32;1m" + "[INFO] Load Main config file SUCCEED." + "\033[0m");

        //控制台输出各配置项
        getLogger().info("[INFO] Check interval time is: " + Interval_time + "s");
        getLogger().info("[INFO] TPS limit is: " + MinTPS);
        getLogger().info("[INFO] TPS keep time: " + TPS_KeepTime + "s");
        if (SmartMode_Enabled)
            getLogger().info("[INFO] SMART mode " + "\033[32;1m" + "Enabled" + "\033[0m");
        else
            getLogger().info("[INFO] SMART mode " + "\033[31;1m" + "Disabled" + "\033[0m");
        if (NoneAI_Enabled)
            getLogger().info("[INFO] NoneAI " + "\033[32;1m" + "Enabled" + "\033[0m");
        else
            getLogger().info("[INFO] NoneAI " + "\033[31;1m" + "Disabled" + "\033[0m");

        //加载实体处理规则
        getLogger().info("[INFO] Loading rules...");
        File RulesFolder = new File(getDataFolder(), "\\rules");    //定位规则文件夹
        if (!RulesFolder.exists())
            RulesFolder.mkdirs();   //不存在则创建
        //加载摧毁实体列表
        {
            Destroy_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\destroy_entity.yml");
            if (!Destroy_Entity_File.exists())
            {//不存在则创建
                //反馈警告
                getLogger().info("\033[33;1m" + "[WARNING] \"destroy_entity.yml\" does not exist. Default rule file will be create." + "\033[0m");
                //新建Default配置副本
                saveResource("rules\\destroy_entity.yml", false);
                //校验
                Destroy_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\destroy_entity.yml");
                Destroy_Entity_Config = YamlConfiguration.loadConfiguration(Destroy_Entity_File);
                if (Destroy_Entity_Config.get("CheckCode") == "DE")
                    getLogger().info("[INFO] Default \"destroy_entity.yml\" loaded.");
                else//原始配置文件错误，报错
                    getLogger().info("\033[31;1m"+ "[ERROR] Default \"destroy_entity.yml\" has been damaged. Please reinstall this plugin" + "\033[0m");
            }
            else
            {//存在则校验
                Destroy_Entity_Config = YamlConfiguration.loadConfiguration(Destroy_Entity_File);
                if (Destroy_Entity_Config.get("CheckCode") == "DE")
                    getLogger().info("[INFO] \"destroy_entity.yml\" loaded.");
                else
                {
                    getLogger().info("\033[33;1m"+ "[WARNING] \"destroy_entity.yml\" has been damaged. Plugin will use default rule file." + "\033[0m");
                    saveResource("rules\\destroy_entity.yml", false);
                    //再校验
                    Destroy_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\destroy_entity.yml");
                    Destroy_Entity_Config = YamlConfiguration.loadConfiguration(Destroy_Entity_File);
                    if (Destroy_Entity_Config.get("CheckCode") == "DE")
                        getLogger().info("[INFO] Default \"destroy_entity.yml\" loaded.");
                    else//原始配置文件错误，报错
                        getLogger().info("\033[31;1m"+ "[ERROR] Default \"destroy_entity.yml\" has been damaged. Please reinstall this plugin" + "\033[0m");
                }
            }
        }
        //加载保留实体列表
        {
            Keep_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\keep_entity.yml");
            if (!Keep_Entity_File.exists())
            {//不存在则创建
                //反馈警告
                getLogger().info("\033[33;1m" + "[WARNING] \"keep_entity.yml\" does not exist. Default rule file will be create." + "\033[0m");
                //新建Default配置副本
                saveResource("rules\\keep_entity.yml", false);
                //校验
                Keep_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\keep_entity.yml");
                Keep_Entity_Config = YamlConfiguration.loadConfiguration(Keep_Entity_File);
                if (Keep_Entity_Config.get("CheckCode") == "KE")
                    getLogger().info("[INFO] Default \"keep_entity.yml\" loaded.");
                else//原始配置文件错误，报错
                    getLogger().info("\033[31;1m"+ "[ERROR] Default \"keep_entity.yml\" has been damaged. Please reinstall this plugin" + "\033[0m");
            }
            else
            {//存在则校验
                Keep_Entity_Config = YamlConfiguration.loadConfiguration(Keep_Entity_File);
                if (Keep_Entity_Config.get("CheckCode") == "KE")
                    getLogger().info("[INFO] \"keep_entity.yml\" loaded.");
                else
                {
                    getLogger().info("\033[33;1m"+ "[WARNING] \"keep_entity.yml\" has been damaged. Plugin will use default rule file." + "\033[0m");
                    saveResource("rules\\keep_entity.yml", false);
                    //再校验
                    Keep_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\keep_entity.yml");
                    Keep_Entity_Config = YamlConfiguration.loadConfiguration(Keep_Entity_File);
                    if (Keep_Entity_Config.get("CheckCode") == "KE")
                        getLogger().info("[INFO] Default \"keep_entity.yml\" loaded.");
                    else//原始配置文件错误，报错
                        getLogger().info("\033[31;1m"+ "[ERROR] Default \"keep_entity.yml\" has been damaged. Please reinstall this plugin" + "\033[0m");
                }
            }
        }
        //加载停用AI实体列表
        {
            RemoveAI_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\removeAI_entity.yml");
            if (!RemoveAI_Entity_File.exists())
            {//不存在则创建
                //反馈警告
                getLogger().info("\033[33;1m" + "[WARNING] \"keep_entity.yml\" does not exist. Default rule file will be create." + "\033[0m");
                //新建Default配置副本
                saveResource("rules\\keep_entity.yml", false);
                //校验
                RemoveAI_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\removeAI_entity.yml");
                RemoveAI_Entity_Config = YamlConfiguration.loadConfiguration(RemoveAI_Entity_File);
                if (RemoveAI_Entity_Config.get("CheckCode") == "RAI")
                    getLogger().info("[INFO] Default \"keep_entity.yml\" loaded.");
                else//原始配置文件错误，报错
                    getLogger().info("\033[31;1m"+ "[ERROR] Default \"keep_entity.yml\" has been damaged. Please reinstall this plugin" + "\033[0m");
            }
            else
            {//存在则校验
                RemoveAI_Entity_Config = YamlConfiguration.loadConfiguration(RemoveAI_Entity_File);
                if (RemoveAI_Entity_Config.get("CheckCode") == "RAI")
                    getLogger().info("[INFO] \"keep_entity.yml\" loaded.");
                else
                {
                    getLogger().info("\033[33;1m"+ "[WARNING] \"keep_entity.yml\" has been damaged. Plugin will use default rule file." + "\033[0m");
                    saveResource("rules\\keep_entity.yml", false);
                    //再校验
                    RemoveAI_Entity_File = new File(RulesFolder.getAbsolutePath() + "\\removeAI_entity.yml");
                    RemoveAI_Entity_Config = YamlConfiguration.loadConfiguration(RemoveAI_Entity_File);
                    if (RemoveAI_Entity_Config.get("CheckCode") == "RAI")
                        getLogger().info("[INFO] Default \"keep_entity.yml\" loaded.");
                    else//原始配置文件错误，报错
                        getLogger().info("\033[31;1m"+ "[ERROR] Default \"keep_entity.yml\" has been damaged. Please reinstall this plugin" + "\033[0m");
                }
            }
        }
    }

    //覆写onEnable()方法
    @Override
    public void onEnable()
    {
        TPS_Monitor TPS_monitor = new TPS_Monitor();
        TPS_monitor.Interval_time = Interval_time;
        TPS_monitor.MinTPS = MinTPS;
        TPS_monitor.TPS_KeepTime = TPS_KeepTime;
        TPS_monitor.SmartMode_Enabled = SmartMode_Enabled;
        TPS_monitor.NoneAI_Enabled = NoneAI_Enabled;

        //创建TPS监视线程
        Thread Monitor_TPS = new Thread(TPS_monitor);
        Monitor_TPS.start();


        getLogger().info("\033[32;1m" + "[INFO] SEL enabled." + "\033[0m");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("\033[31;1m" + "[INFO] SEL disabled." + "\033[0m");
    }
}
