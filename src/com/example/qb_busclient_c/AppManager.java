package com.example.qb_busclient_c;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
 
/**
 * 应用程序Activity管理类
 * @author  jzt
 */
public class AppManager 
{
    private static Stack<Activity> qb_activityStack;
    private static AppManager instance;
     
    private AppManager(){}
    /**
     * 单一实例
     */
    public static AppManager getAppManager()
    {
        if(instance==null)
        {
            instance=new AppManager();
        }
        return instance;
    }
    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity)
    {
        if(qb_activityStack==null)
        {
        	qb_activityStack=new Stack<Activity>();
        }
        qb_activityStack.add(activity);
    }
    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity()
    {
        Activity activity=qb_activityStack.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity()
    {
        Activity activity=qb_activityStack.lastElement();
        if(activity!=null)
        {
        	qb_activityStack.remove(activity);
        	if(activity.isFinishing() == false)
        	{
        		activity.finish();
        		activity=null;
        	}
        }
    }
    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity)
    {
        if(activity!=null)
        {
        	qb_activityStack.remove(activity);
        	if(activity.isFinishing() == false)
        	{
        		activity.finish();
        		activity=null;
        	}
        }
    }
    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls)
    {
        for (Activity activity : qb_activityStack) 
        {
            if(activity.getClass().equals(cls) )
            {
                finishActivity(activity);
            }
        }
    }
    /**
     * 结束所有Activity
     */
    public void finishAllActivity()
    {
        while(qb_activityStack.size() > 0)
        {
            if (null != qb_activityStack.get(0) && qb_activityStack.get(0).isFinishing() == false)
            {
            	qb_activityStack.get(0).finish();
            }
            qb_activityStack.remove(qb_activityStack.get(0));
        }
        qb_activityStack.clear();
    }
    /**
     * 退出应用程序
     */
    public void AppExit(Context context) 
    {
        try 
        {
            finishAllActivity();
            ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) { }
    }
}