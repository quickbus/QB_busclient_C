package com.example.qb_busclient_c;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
 
/**
 * Ӧ�ó���Activity������
 * @author  jzt
 */
public class AppManager 
{
    private static Stack<Activity> qb_activityStack;
    private static AppManager instance;
     
    private AppManager(){}
    /**
     * ��һʵ��
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
     * ���Activity����ջ
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
     * ��ȡ��ǰActivity����ջ�����һ��ѹ��ģ�
     */
    public Activity currentActivity()
    {
        Activity activity=qb_activityStack.lastElement();
        return activity;
    }
    /**
     * ������ǰActivity����ջ�����һ��ѹ��ģ�
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
     * ����ָ����Activity
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
     * ����ָ��������Activity
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
     * ��������Activity
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
     * �˳�Ӧ�ó���
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