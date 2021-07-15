package cn.OctAutumn.SmartEntityLimit;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.text.Component.text;
import static org.bukkit.Bukkit.getTPS;

public class TPS_Monitor implements Runnable
{
    //核心类引用
    Core MainClass = null;

    public TPS_Monitor(Core core)
    {
        MainClass = core;
    }

    //启用标记
    public boolean Enabled = true;  //标记为false时监视器线程将终止，且此标记自恢复为true

    //标识
    public volatile boolean Monitor_Running = false;  //TPS监视器运行 可用于长时间暂停监视器
    public boolean Monitor_Thread_Wait = false; //监视器线程等待0.5s

    //Json生成器
    JsonTextElement JsonMsg = new JsonTextElement();

    public void stop()
    {//停止TPS监视线程（顺序不能颠倒，否则将导致线程无法正常终止）
        Enabled = false;
        Monitor_Running = false;
    }

    @Override
    public void run()
    {
        JsonMsg.BuilderInitialize();
        JsonMsg.MsgBuilder.append(text("即将进行TPS检查~", Style.style(TextColor.color(169, 169, 169))));
        TextComponent TPS_CheckMsg = JsonMsg.MsgBuilder.build();

        while (Enabled)
        {
            //长时间暂停
            while (!Monitor_Running)
            {
                Thread.onSpinWait();
            }
            //其它线程要求短时间暂停监视器
            if (Monitor_Thread_Wait)
            {
                try
                {//暂停0.5s 可进行线程通讯
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                //此处进行线程通讯

                Monitor_Thread_Wait = false;
            }

            try
            {//等待时间，TPS检查间隔
                Thread.sleep(MainClass.Interval_time * 1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            //广播TPS检查消息
            JsonMsg.BroadCast(TPS_CheckMsg);

            double[] sever_TPS = getTPS();

            if (sever_TPS[0] < MainClass.TPS_Trigger)
            {
                try
                {//1s后重测，防止TPS波动引起误清理
                    Thread.sleep(1000);
                    sever_TPS = getTPS();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (sever_TPS[0] < MainClass.TPS_Trigger)
                {//TPS仍然低于触发值
                    //广播TPS过低消息
                    JsonMsg.BuilderInitialize();
                    JsonMsg.MsgBuilder.append(text("唔~服务器TPS低于标准咯（1min：" + String.format("%.02f", sever_TPS[0]) + "/"+String.format("%.02f", MainClass.TPS_Trigger)+"），将在15秒后开始清理", Style.style(TextColor.color(255, 250, 205), TextDecoration.BOLD)));
                    JsonMsg.BroadCast();

                    try
                    {//暂停15s
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    MainClass.LimitOperator.operate();

                    while (!Monitor_Running)
                    {
                        Thread.onSpinWait();
                    }
                }


            }
        }
        //自动复位，下次运行时直接调用start()即可
        Enabled = true;

    }
}
