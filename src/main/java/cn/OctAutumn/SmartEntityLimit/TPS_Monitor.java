package cn.OctAutumn.SmartEntityLimit;

public class TPS_Monitor implements Runnable
{
    public int Interval_time = 600;
    public double MinTPS = 15.00d;
    public int TPS_KeepTime = 30;
    public boolean SmartMode_Enabled = true;
    public boolean NoneAI_Enabled = false;

    //标识
    public boolean Monitor_Enabled = true;  //启用TPS监视器
    public boolean Monitor_Thread_Wait = false; //监视器线程等待

    @Override
    public void run()
    {

    }
}
