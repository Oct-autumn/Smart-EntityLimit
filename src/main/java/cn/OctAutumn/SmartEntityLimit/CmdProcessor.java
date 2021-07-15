package cn.OctAutumn.SmartEntityLimit;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class CmdProcessor implements TabExecutor
{
    //核心类引用
    Core MainClass = null;

    public CmdProcessor(Core core)
    {
        MainClass = core;
    }

    //启用标记
    public boolean Enabled = true;

    //Json生成器
    JsonTextElement JsonMsg = new JsonTextElement();

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String s, String[] parameter)
    {
        if (!Enabled)
        {//插件禁用状态下，不启用
            sender.sendMessage("[INFO] SEL disabled.");
            return false;
        }

        {
            if (sender instanceof Player)
            {//玩家，花里胡哨一点(暂不开放服务器内部调整)
                switch (parameter[0])
                {
                    case "show":
                        switch (parameter[1])
                        {
                            case "Tps_CheckInterval":
                            case "Tps_Trigger":
                            case "Tps_KeepTime":
                            case "SmartMode":
                            case "NoneAI":
                        }
                    case "set":
                        switch (parameter[1])
                        {
                            case "Tps_CheckInterval":
                            case "Tps_Trigger":
                            case "Tps_KeepTime":
                            case "SmartMode":
                            case "NoneAI":
                        }
                    case "CleanNow_OpConfirm":    //不能用Tab补全，防止误操作
                        //广播清理通知
                        JsonMsg.BuilderInitialize();
                        JsonMsg.MsgBuilder.append(text("呜~ 应 Op." + sender.getName() + " 的要求，将于30秒后开始清理实体", Style.style(TextColor.color(255, 250, 205), TextDecoration.BOLD)));
                        JsonMsg.BroadCast();

                        MainClass.LimitOperator.operate(30);

                        return true;
                }
                sender.sendMessage("[SEL][暂不开放服务器内部调整参数的功能]");
            } else
            {//控制台，简单一点
                switch (parameter[0])
                {
                    case "show":
                        switch (parameter[1])
                        {//根据第二个参数显示各项配置
                            case "Tps_CheckInterval":
                                sender.sendMessage("[Smart Entity Limit] [INFO] TPS Check Interval is " + MainClass.Interval_time + "sec");
                                return true;
                            case "Tps_Trigger":
                                sender.sendMessage("[Smart Entity Limit] [INFO] Trigger TPS is " + MainClass.TPS_Trigger);
                                return true;
                            case "Tps_KeepTime":
                                sender.sendMessage("[Smart Entity Limit] [INFO] TPS keep time is " + MainClass.TPS_KeepTime + "sec");
                                return true;
                            case "SmartMode":
                                if (MainClass.SmartMode_Enabled)
                                    sender.sendMessage("[Smart Entity Limit] [INFO] Smart mode is " + "\033[32;1m" + "Enabled" + "\033[0m");
                                else
                                    sender.sendMessage("[Smart Entity Limit] [INFO] Smart mode is " + "\033[31;1m" + "Disabled" + "\033[0m");
                                return true;
                            case "NoneAI":
                                if (MainClass.NoneAI_Enabled)
                                    sender.sendMessage("[Smart Entity Limit] [INFO] NoneAI mode is " + "\033[32;1m" + "Enabled" + "\033[0m");
                                else
                                    sender.sendMessage("[Smart Entity Limit] [INFO] NoneAI mode is " + "\033[31;1m" + "Disabled" + "\033[0m");
                                return true;
                            default:
                                sender.sendMessage("[SEL CMD] Example:\"\\SmartEL <set/show> <var> <@Nullable>\"");
                        }
                    case "set":
                        switch (parameter[1])
                        {
                            case "Tps_CheckInterval":
                                long Interval_time = Long.parseLong(parameter[2]);
                                if (Interval_time < 60)
                                {//TPS检查间隔不得短于1min
                                    sender.sendMessage("\033[33;1m" + "[Smart Entity Limit] [INFO] The TPS Check Interval must no less than 60 sec." + "\033[0m");
                                    return true;
                                }
                                MainClass.Interval_time = Interval_time;
                                MainClass.MainConfig.set("Interval time", MainClass.Interval_time);
                                MainClass.saveConfig();
                                sender.sendMessage("[Smart Entity Limit] [INFO] Now TPS Check Interval is " + MainClass.Interval_time + "sec");
                                return true;
                            case "Tps_Trigger":
                                double TPS_Trigger = Double.parseDouble(parameter[2]);
                                if ((TPS_Trigger < 0) || (TPS_Trigger - 20.00d > 0))
                                {//TPS触发值不得为负或超过20.00
                                    sender.sendMessage("\033[33;1m" + "[Smart Entity Limit] [INFO] The Trigger TPS must between 0 and 20.00." + "\033[0m");
                                    return true;
                                }
                                MainClass.TPS_Trigger = TPS_Trigger;
                                MainClass.MainConfig.set("MinTPS", MainClass.TPS_Trigger);
                                MainClass.saveConfig();
                                sender.sendMessage("[Smart Entity Limit] [INFO] Now Trigger TPS is " + MainClass.TPS_Trigger);
                                return true;
                            case "Tps_KeepTime":
                                int TPS_KeepTime = Integer.parseInt(parameter[2]);
                                if (TPS_KeepTime > MainClass.Interval_time)
                                {//TPS保持时间不得长于检查间隔
                                    sender.sendMessage("\033[33;1m" + "[Smart Entity Limit] [INFO] The TPS Keep Time must shorter than TPS Check Interval." + "\033[0m");
                                    return true;
                                }
                                if (TPS_KeepTime < 0)
                                {//TPS保持时间不得为负
                                    sender.sendMessage("\033[33;1m" + "[Smart Entity Limit] [INFO] The TPS Keep Time must be positive." + "\033[0m");
                                    return true;
                                }
                                MainClass.TPS_KeepTime = TPS_KeepTime;
                                MainClass.MainConfig.set("Keep time", MainClass.TPS_KeepTime);
                                MainClass.saveConfig();
                                sender.sendMessage("[Smart Entity Limit] [INFO] Now TPS Keep Time is " + MainClass.TPS_KeepTime + "sec");
                                return true;
                            case "SmartMode":
                                if (parameter[2].equals("enabled") || parameter[2].equals("Enabled"))
                                {
                                    MainClass.SmartMode_Enabled = true;
                                    MainClass.MainConfig.set("Enable SMART mode", MainClass.SmartMode_Enabled);
                                    MainClass.saveConfig();
                                    sender.sendMessage("[Smart Entity Limit] [INFO] Now Smart mode is " + "\033[32;1m" + "Enabled" + "\033[0m");
                                    return true;
                                }
                                if (parameter[2].equals("disabled") || parameter[2].equals("Disabled"))
                                {
                                    MainClass.SmartMode_Enabled = false;
                                    MainClass.MainConfig.set("Enable SMART mode", MainClass.SmartMode_Enabled);
                                    MainClass.saveConfig();
                                    sender.sendMessage("[Smart Entity Limit] [INFO] Now Smart mode is " + "\033[31;1m" + "Disabled" + "\033[0m");
                                    return true;
                                }
                                sender.sendMessage("\033[33;1m" + "[Smart Entity Limit] [INFO] Illegal input." + "\033[0m");
                                return true;
                            case "NoneAI":
                                if (parameter[2].equals("enabled") || parameter[2].equals("Enabled"))
                                {
                                    MainClass.NoneAI_Enabled = true;
                                    MainClass.MainConfig.set("Enable NoneAI", MainClass.NoneAI_Enabled);
                                    MainClass.saveConfig();
                                    sender.sendMessage("[Smart Entity Limit] [INFO] Now NoneAI mode is " + "\033[32;1m" + "Enabled" + "\033[0m");
                                    return true;
                                }
                                if (parameter[2].equals("disabled") || parameter[2].equals("Disabled"))
                                {
                                    MainClass.NoneAI_Enabled = false;
                                    MainClass.MainConfig.set("Enable SMART mode", MainClass.NoneAI_Enabled);
                                    MainClass.saveConfig();
                                    sender.sendMessage("[Smart Entity Limit] [INFO] Now NoneAI mode is " + "\033[31;1m" + "Disabled" + "\033[0m");
                                    return true;
                                }
                                sender.sendMessage("\033[33;1m" + "[Smart Entity Limit] [INFO] Illegal input." + "\033[0m");
                                return true;
                        }
                }
            }
        }

        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] parameter)
    {
        if (!Enabled)
        {//插件禁用状态下，不启用
            sender.sendMessage("[INFO] SEL disabled.");
            return new ArrayList<>();
        }

        String[] L1_Commands = {"show", "set"};
        String[] L2_Commands = {"Tps_CheckInterval", "Tps_Trigger", "Tps_KeepTime", "SmartMode", "NoneAI"};
        String[] Bool_Commands = {"enabled", "disabled"};

        //Tab命令补全
        switch (parameter.length)
        {
            case 1:
                return Arrays.asList(L1_Commands); //零级命令补全
            case 2:
                return Arrays.asList(L2_Commands); //一级命令补全
            case 3:
                //根据零级命令确定是“获取”还是“设定”
                if (parameter[0].equals("set"))
                    //“设定”状态下判定是哪个项目的设定，智能模式和AI抑制模式下提供可用量（ps.经常有人拼错（半恼））
                    //当前，智能模式、AI抑制模式暂时无效
                    if (parameter[1].equals("SmartMode") || parameter[1].equals("NoneAI"))
                        return Arrays.asList(Bool_Commands);
                //其它情况不返回补全列表
                return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }
}