package cn.OctAutumn.SmartEntityLimit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.util.Objects;

public class Core extends JavaPlugin
{
    //主配置文件
    FileConfiguration MainConfig = null;

    //规则配置文件类
    public class RulesFile
    {
        String FileName = "";
        String CheckCode = "";
        File Rule_File = null;
        FileConfiguration Rule_Config = null;

        public void setFileInfo(String file_name, String check_code)
        {
            FileName = file_name + ".yml";
            CheckCode = check_code;
        }

        public File getRule_File()
        {
            return Rule_File;
        }

        public FileConfiguration getRule_Config()
        {
            return Rule_Config;
        }

        public boolean LoadRulesFile()
        {
            //--定位规则文件夹--
            File RulesFolder = new File(getDataFolder(), "\\rules");
            if (!RulesFolder.exists())
                RulesFolder.mkdirs();   //不存在则创建
            this.Rule_File = new File(RulesFolder.getAbsolutePath() + "\\" + this.FileName);
            if (!this.Rule_File.exists())
            {//不存在则创建
                //反馈警告
                getLogger().info("\033[33;1m" + "[WARNING] \"" + this.FileName + "\" does not exist. Default rule file will be create." + "\033[0m");
                //新建Default配置副本
                saveResource("rules\\" + this.FileName, false);
                //校验
                this.Rule_File = new File(RulesFolder.getAbsolutePath() + "\\" + this.FileName);
                this.Rule_Config = YamlConfiguration.loadConfiguration(this.Rule_File);
                if (this.Rule_Config.getString("CheckCode", "UN").equals(this.CheckCode))
                {
                    getLogger().info("[INFO] Default \"" + this.FileName + "\" loaded.");
                    return true;
                } else
                {//原始配置文件错误，报错
                    getLogger().info("\033[31;1m" + "[ERROR] Default \"" + this.FileName + "\" has been damaged. Please reinstall this plugin" + "\033[0m");
                    return false;
                }
            } else
            {//存在则校验
                this.Rule_Config = YamlConfiguration.loadConfiguration(this.Rule_File);
                if (this.Rule_Config.getString("CheckCode", "UN").equals(this.CheckCode))
                    getLogger().info("[INFO] \"" + this.FileName + "\" loaded.");
                else
                {
                    getLogger().info("\033[33;1m" + "[WARNING] \"" + this.FileName + "\" has been damaged. Plugin will use default rule file." + "\033[0m");
                    saveResource("rules\\" + this.FileName, true);
                    //再校验
                    this.Rule_File = new File(RulesFolder.getAbsolutePath() + "\\" + this.FileName);
                    this.Rule_Config = YamlConfiguration.loadConfiguration(this.Rule_File);
                    if (this.Rule_Config.getString("CheckCode", "UN").equals(this.CheckCode))
                        getLogger().info("[INFO] Default \"" + this.FileName + "\" loaded.");
                    else
                    {//原始配置文件错误，报错
                        getLogger().info("\033[31;1m" + "[ERROR] Default \"" + this.FileName + "\" has been damaged. Please reinstall this plugin" + "\033[0m");
                        return false;
                    }
                }
            }
            return false;
        }

        public boolean SaveRulesFile()
        {
            if (Rule_File == null)
            {
                getLogger().info("\033[31;1m" + "[ERROR] \"" + this.FileName + "\" has not been loaded yet." + "\033[0m");
                return false;
            }
            if (Rule_Config == null)
            {
                getLogger().info("\033[31;1m" + "[ERROR] The configuration corresponding to \"" + this.FileName + "\" has not been loaded yet." + "\033[0m");
                return false;
            }

            try
            {
                Rule_Config.save(Rule_File);
            } catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    //规则配置文件
    public RulesFile Destroy_Entity = new RulesFile();
    public RulesFile Keep_Entity = new RulesFile();
    public RulesFile RemoveAI_Entity = new RulesFile();

    //配置变量 及 默认配置
    public long Interval_time = 600;
    public double TPS_Trigger = 15.00d;
    public int TPS_KeepTime = 30;
    public boolean SmartMode_Enabled = true;
    public boolean NoneAI_Enabled = false;

    //
    public TPS_Monitor TPS_monitor = new TPS_Monitor(this);
    public CmdProcessor CmdProcess = new CmdProcessor(this);

    //覆写onLoad()方法
    @Override
    public void onLoad()
    {
        saveDefaultConfig();
        getLogger().info("---Built by OctAutumn---");
        //--加载主配置文件--
        getLogger().info("[INFO] Loading config file...");
        MainConfig = getConfig();
        //反馈加载成功，使用配置文件中的配置
        Interval_time = MainConfig.getLong("Interval time");
        TPS_Trigger = MainConfig.getDouble("MinTPS");
        TPS_KeepTime = MainConfig.getInt("Keep time");
        SmartMode_Enabled = MainConfig.getBoolean("Enable SMART mode");
        NoneAI_Enabled = MainConfig.getBoolean("Enable NoneAI");
        getLogger().info("\033[32;1m" + "[INFO] Load Main config file SUCCEED." + "\033[0m");

        //控制台输出各配置项
        getLogger().info("[INFO] Check interval time is: " + Interval_time + "s");
        getLogger().info("[INFO] TPS limit is: " + TPS_Trigger);
        getLogger().info("[INFO] TPS keep time: " + TPS_KeepTime + "s");
        if (SmartMode_Enabled)
            getLogger().info("[INFO] SMART mode " + "\033[32;1m" + "Enabled" + "\033[0m");
        else
            getLogger().info("[INFO] SMART mode " + "\033[31;1m" + "Disabled" + "\033[0m");
        if (NoneAI_Enabled)
            getLogger().info("[INFO] NoneAI " + "\033[32;1m" + "Enabled" + "\033[0m");
        else
            getLogger().info("[INFO] NoneAI " + "\033[31;1m" + "Disabled" + "\033[0m");

        //--加载实体处理规则--
        getLogger().info("[INFO] Loading rules...");
        //加载销毁实体列表
        Destroy_Entity.setFileInfo("destroy_entity", "DE");
        Destroy_Entity.LoadRulesFile();
        //加载保留实体列表
        Keep_Entity.setFileInfo("keep_entity", "KE");
        Keep_Entity.LoadRulesFile();
        //加载AI抑制实体列表
        RemoveAI_Entity.setFileInfo("removeAI_entity", "RE");
        RemoveAI_Entity.LoadRulesFile();
    }

    //覆写onEnable()方法
    @Override
    public void onEnable()
    {
        //注册命令实现与补全
        Objects.requireNonNull(Bukkit.getPluginCommand("smart-entity-limit")).setExecutor(CmdProcess);
        Objects.requireNonNull(Bukkit.getPluginCommand("smart-entity-limit")).setTabCompleter(CmdProcess);
        //创建TPS监视线程
        // Thread Monitor_TPS = new Thread(TPS_monitor);
        // Monitor_TPS.start();

        enabled();
        getLogger().info("\033[32;1m" + "[INFO] SEL enabled." + "\033[0m");
    }

    @Override
    public void onDisable()
    {
        disable();
        getLogger().info("\033[31;1m" + "[INFO] SEL disabled." + "\033[0m");
    }

    public void enabled()
    {
        new CmdProcessor(this).Enabled = true;
        new TPS_Monitor(this).Enabled = true;
    }

    public void disable()
    {
        new CmdProcessor(this).Enabled = false;
        new TPS_Monitor(this).Enabled = false;
    }
}
