package cn.OctAutumn.SmartEntityLimit;

import com.sun.tools.javac.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.*;

public class EntityLimit
{
    Core MainClass;

    public EntityLimit(Core core)
    {
        MainClass = core;
    }

    //Json生成器
    JsonTextElement JsonMsg = new JsonTextElement();

    //实体清理方法
    public synchronized void Operator()
    {
            //5个数值，分别对应着：[0]搜索到的实体数、[1]清除实体数、[2]保留实体数、[3]AI抑制实体数、[4]剩余实体数
            int EntitySum[] = {0, 0, 0, 0, 0};



            List<World> WorldList = Bukkit.getServer().getWorlds();

            //遍历各世界
            for (World NowWorld : WorldList)
            {
                List<Entity> EntityList = NowWorld.getEntities();
                //遍历各实体
                for (Entity NowEntity : EntityList)
                {
                    EntitySum[0]++; //记录总实体数

                    String NowEntityType = NowEntity.getType().name().toLowerCase();

                    if ((!NowEntityType.equals("player")) && (NowEntity.getName().toLowerCase().equals(NowEntityType.replace("_"," "))))
                    {
                        if (MainClass.Destroy_Entity.RuleEntity.contains(NowEntityType))
                        {
                            NowEntity.remove();
                            EntitySum[1]++;
                        }
                        else
                        {
                            EntitySum[2]++;
                        }

                    } else
                    {
                        EntitySum[2]++;
                    }


                }
            }

            EntitySum[4] = EntitySum[0] - EntitySum[1];

            {//清理后汇报
                JsonMsg.MsgBuilder.append(Component.text("刚刚一共搜索到了", Style.style(TextColor.color(0, 255, 127))));
                JsonMsg.MsgBuilder.append(Component.text(String.valueOf(EntitySum[0]), Style.style(TextColor.color(152, 245, 255), TextDecoration.BOLD)));
                JsonMsg.MsgBuilder.append(Component.text("个实体，删除了", Style.style(TextColor.color(0, 255, 127))));
                JsonMsg.MsgBuilder.append(Component.text(String.valueOf(EntitySum[1]), Style.style(TextColor.color(152, 245, 255), TextDecoration.BOLD)));
                JsonMsg.MsgBuilder.append(Component.text("个，保留了", Style.style(TextColor.color(0, 255, 127))));
                JsonMsg.MsgBuilder.append(Component.text(String.valueOf(EntitySum[2]), Style.style(TextColor.color(152, 245, 255), TextDecoration.BOLD)));
                JsonMsg.MsgBuilder.append(Component.text("个，AI抑制了", Style.style(TextColor.color(0, 255, 127))));
                JsonMsg.MsgBuilder.append(Component.text(String.valueOf(EntitySum[3]), Style.style(TextColor.color(152, 245, 255), TextDecoration.BOLD)));
                JsonMsg.MsgBuilder.append(Component.text("个，最终剩余实体", Style.style(TextColor.color(0, 255, 127))));
                JsonMsg.MsgBuilder.append(Component.text(String.valueOf(EntitySum[4]), Style.style(TextColor.color(152, 245, 255), TextDecoration.BOLD)));
                JsonMsg.MsgBuilder.append(Component.text("个.", Style.style(TextColor.color(0, 255, 127))));
                JsonMsg.BroadCast();
            }
    }

    //带延时的清理
    public void operate(int time)
    {
        Thread T = new Thread(() ->
        {
            try
            {
                Thread.sleep(time * 1000L);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Operator();
        });
        T.start();
    }

    //不带延时的清理
    public void operate()
    {
        Operator();
    }
}
