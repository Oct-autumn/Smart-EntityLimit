package cn.OctAutumn.SmartEntityLimit;


import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import static net.kyori.adventure.text.Component.text;

public class JsonTextElement
{
    TextComponent.Builder MsgBuilder = text();
    TextComponent MsgPermisson;

    public JsonTextElement()
    {
        //可定制标头
        MsgBuilder.append(text("[",Style.style(TextColor.color(105,105,105), TextDecoration.BOLD)));
        MsgBuilder.append(text("魔", Style.style(TextColor.color(255,69,0), TextDecoration.BOLD)));
        MsgBuilder.append(text("法", Style.style(TextColor.color(204,255,0), TextDecoration.BOLD)));
        MsgBuilder.append(text("羊", Style.style(TextColor.color(0,255,102), TextDecoration.BOLD)));
        MsgBuilder.append(text("女", Style.style(TextColor.color(0,102,255), TextDecoration.BOLD)));
        MsgBuilder.append(text("仆", Style.style(TextColor.color(187,0,255), TextDecoration.BOLD)));
        MsgBuilder.append(text("]", Style.style(TextColor.color(105,105,105), TextDecoration.BOLD)));

        MsgPermisson = MsgBuilder.build();

        BuilderInitialize();    //初始化生成器
    }

    public void BuilderInitialize()
    {
        MsgBuilder = text();
        MsgBuilder.append(MsgPermisson);
    }

    public void BroadCast(TextComponent Msg)
    {
        Bukkit.getServer().broadcast(Msg);
    }

    public void BroadCast()
    {
        Bukkit.getServer().broadcast(MsgBuilder.build());
        BuilderInitialize();
    }


}
