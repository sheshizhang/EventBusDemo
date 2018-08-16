package com.eventbus.demo.eventBus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBus {
    private Handler handler;
    private ExecutorService executorService;//线程
    private Map<Object,List<SubscribleMethod>>cacheMap;//保存事件处理总表

    public static EventBus getDefault() {
        return EventBusHolder.instance;
    }

    public static class EventBusHolder{
        private static EventBus instance=new EventBus();
    }

    public EventBus(){
        handler=new Handler(Looper.getMainLooper());
        executorService= Executors.newCachedThreadPool();
        cacheMap=new HashMap<>();
    }

    /**
     * EventBus注册
     * @param activity
     */
    public void register(Object activity){
        List<SubscribleMethod>list=cacheMap.get(activity);
        if (list==null){
            list=getSubscribleMethods(activity);
            cacheMap.put(activity,list);
        }
    }

    /**
     * EventBus注销
     * @param activity
     */
    public void unregister(Object activity){
        List<SubscribleMethod>list=cacheMap.get(activity);
        if (list!=null){
            cacheMap.remove(activity);
        }
    }

    /**
     * 发送事件
     * @param friend
     */
    public void post(final Object friend){
        //遍历查找到的subscribe 方法
        Set<Object>set=cacheMap.keySet();

        Iterator iterator=set.iterator();
        while(iterator.hasNext()){
            final Object value=iterator.next();
            List<SubscribleMethod>list=cacheMap.get(value);

            for (final SubscribleMethod subscribleMethod:list){
                ThreadMode threadMode=subscribleMethod.getThreadMode();

                switch (threadMode){
                    case MainThread://主线程调用
                        if (Looper.myLooper()==Looper.getMainLooper()){
                            invoke(subscribleMethod,value,friend);
                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subscribleMethod,value,friend);
                                }
                            });
                        }
                        break;
                    case Async://异步线程
                        if (Looper.myLooper()==Looper.getMainLooper()){
                            //执行在主线程，切换线程
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    Log.w("TAG","子线程预定");
                                    invoke(subscribleMethod,value,friend);
                                }
                            });
                        }else{
                            //执行在子线程
                            invoke(subscribleMethod,value,friend);
                        }
                        break;
                    case PostThread:

                        break;
                    case BackgroundThread:

                        break;
                }
            }
        }
    }

    private void invoke(SubscribleMethod subscribleMethod, Object value, Object friend) {
        Method method=subscribleMethod.getMethod();

        try {
            method.invoke(value,friend);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射得到Subscrible处理事件的方法
     * @return
     */
    public List<SubscribleMethod> getSubscribleMethods(Object activity) {
        List<SubscribleMethod>list=new ArrayList<>();

        Class<?>classType=activity.getClass();

        while (classType!=null){
            String className=classType.getName();
            if (className.startsWith("java.")||className.startsWith("javax.")
                    ||className.startsWith("android.")){
                break;
            }
            //符合要求
            Method[]methods=classType.getDeclaredMethods();
            Log.w("zhangfeiran100","methods.length=="+methods.length);
            for (Method method:methods){
                Subscribe subscribe=method.getAnnotation(Subscribe.class);
                if (subscribe==null){
                    continue;
                }
                //监测这个方法符不符合
                Class<?>[]paramtypes=method.getParameterTypes();
                if (paramtypes.length!=1){
                    throw new RuntimeException("只能有一个参数");
                }

                //符合要求
                ThreadMode threadMode=subscribe.threadMode();
                SubscribleMethod subscribleMethod=new SubscribleMethod(method,threadMode,paramtypes[0]);
                list.add(subscribleMethod);
            }
            classType=classType.getSuperclass();
        }
        return list;
    }



}
