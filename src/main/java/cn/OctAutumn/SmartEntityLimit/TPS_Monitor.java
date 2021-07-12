package cn.OctAutumn.SmartEntityLimit;

import com.sun.tools.javac.Main;

import static org.bukkit.Bukkit.getConsoleSender;
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
    public boolean Enabled = true;

    //标识
    public boolean Monitor_Running = false;  //TPS监视器运行
    public boolean Monitor_Thread_Wait = false; //监视器线程等待

    @Override
    public void run()
    {

        while (true)
        {
            while (Monitor_Running)
            {
                try
                {//每隔5s检查一次运行状态 可进行线程通讯
                    wait(5000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            //主线程要求暂停监视器
            if (Monitor_Thread_Wait)
            {
                try
                {//暂停0.5s 可进行线程通讯
                    wait(500);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                Monitor_Thread_Wait = false;
            }

            try
            {//等待时间，TPS检查间隔
                wait(MainClass.Interval_time * 1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            double sever_TPS[] = getTPS();

            getConsoleSender().sendMessage("Hello World");

        }


    }
}
