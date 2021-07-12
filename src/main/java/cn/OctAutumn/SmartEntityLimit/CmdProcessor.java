package cn.OctAutumn.SmartEntityLimit;

import com.sun.tools.javac.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String s, String[] parameter)
    {
        if (!Enabled)
        {//插件禁用状态下，不启用
            sender.sendMessage("[INFO] SEL disabled.");
            return false;
        }

        /*
        if (parameter[0].equals("show"))
            if (parameter[1].equals("Tps_CheckInterval"))
            {
                sender.sendMessage("[SEL INFO] Command receive");
                sender.sendMessage(new Core().Interval_time + "s");
                return true;
            }
         */

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
                            default:
                        }
                    case "set":
                        switch (parameter[1])
                        {
                            case "Tps_CheckInterval":
                            case "Tps_Trigger":
                            case "Tps_KeepTime":
                            case "SmartMode":
                            case "NoneAI":
                            default:
                        }
                    default:
                        sender.sendMessage("Example:\"\\SmartEL <set/show> <var> <@Nullable>\"");
                }
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
                                sender.sendMessage("[Smart Entity Limit] [INFO] Now TPS Check Interval is " + MainClass.Interval_time + "sec");
                                return true;
                            case "Tps_Trigger":
                                sender.sendMessage("[Smart Entity Limit] [INFO] Trigger TPS is " + MainClass.TPS_Trigger);
                                return true;
                            case "Tps_KeepTime":
                                return true;
                            case "SmartMode":
                                return true;
                            case "NoneAI":
                                return true;
                            default:
                        }
                    default:
                        sender.sendMessage("[SEL CMD] Example:\"\\SmartEL <set/show> <var> <@Nullable>\"");
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