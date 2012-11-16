package com.sky.sms.adv.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HandlerTest extends Activity implements OnClickListener{   
    
    private String TAG = "HandlerTest";   
    private boolean bpostRunnable = false;   
       
    private NoLooperThread noLooperThread = null;     
    private OwnLooperThread ownLooperThread = null;     
    private ReceiveMessageThread receiveMessageThread =null;   
       
    private Handler mOtherThreadHandler=null;   
    private EventHandler mHandler = null;    
       
    private Button btn1 = null;   
    private Button btn2 = null;   
    private Button btn3 = null;   
    private Button btn4 = null;   
    private Button btn5 = null;   
    private Button btn6 = null;   
    private TextView tv = null;   
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
          
        LinearLayout layout = new LinearLayout(this);   
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 50);   
        layout.setOrientation(LinearLayout.VERTICAL);   
           
        btn1 = new Button(this);   
        btn1.setId(101);   
        btn1.setText("message from main thread self");   
        btn1.setOnClickListener(this);   
        layout.addView(btn1, params);   
           
        btn2 = new Button(this);   
        btn2.setId(102);   
        btn2.setText("message from other thread to main thread");   
        btn2.setOnClickListener(this);   
        layout.addView(btn2,params);   
           
        btn3 = new Button(this);   
        btn3.setId(103);   
        btn3.setText("message to other thread from itself");   
        btn3.setOnClickListener(this);   
        layout.addView(btn3, params);   
           
        btn4 = new Button(this);   
        btn4.setId(104);   
        btn4.setText("message with Runnable as callback from other thread to main thread");   
        btn4.setOnClickListener(this);   
        layout.addView(btn4, params);   
           
        btn5 = new Button(this);   
        btn5.setId(105);   
        btn5.setText("main thread's message to other thread");   
        btn5.setOnClickListener(this);   
        layout.addView(btn5, params);   
           
        btn6 = new Button(this);   
        btn6.setId(106);   
        btn6.setText("exit");   
        btn6.setOnClickListener(this);   
        layout.addView(btn6, params);   
           
        tv = new TextView(this);   
        tv.setTextColor(Color.WHITE);   
        tv.setText("");   
        params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);   
        params.topMargin=10;   
        layout.addView(tv, params);   
        setContentView(layout);   
        receiveMessageThread = new ReceiveMessageThread();   
        receiveMessageThread.start();   
           
    }   
       
    class EventHandler extends Handler{   
           
        public EventHandler(Looper looper){   
            super(looper);   
        }   
           
        public EventHandler(){   
             super();   
         }   
         @Override  
         public void handleMessage(Message msg) {   
             super.handleMessage(msg);   
             Log.e(TAG, "CurrentThread id:----------+>" + Thread.currentThread().getId());   
             switch(msg.what){   
             case 1:   
                 tv.setText((String)msg.obj);   
                 break;   
                    
             case 2:   
                 tv.setText((String)msg.obj);   
                 noLooperThread.stop();   
                 break;   
                    
             case 3:   
                 //不能在非主线程的线程里面更新UI，所以这里通过log打印信息   
                 Log.e(TAG,(String)msg.obj);   
                 ownLooperThread.stop();   
                 break;   
             default:   
                 Log.e(TAG,(String)msg.obj);   
                 break;   
             }   
         }   
            
     }   
        
       //ReceiveMessageThread has his own message queue by execute Looper.prepare();    
     class ReceiveMessageThread extends Thread {   
         @Override  
         public void run(){   
             Looper.prepare();   
             mOtherThreadHandler= new Handler(){   
                 @Override  
                 public void handleMessage(Message msg) {   
                     super.handleMessage(msg);   
                     Log.e(TAG,"-------+>"+(String)msg.obj);   
                     Log.e(TAG, "CurrentThread id:----------+>" + Thread.currentThread().getId());   
                 }   
              };   
              Log.e(TAG, "ReceiveMessageThread id:--------+>" + this.getId());    
              Looper.loop();   
         }   
     }   
        
     class NoLooperThread extends Thread {   
         private EventHandler mNoLooperThreadHandler;   
         @Override  
         public void run() {   
             Looper myLooper = Looper.myLooper();   
             Looper mainLooper= Looper.getMainLooper();   
             String msgobj;   
             if(null == myLooper){   
                 //这里获得的是主线程的Looper,由于NoLooperThread没有自己的looper所以这里肯定会被执行   
                 mNoLooperThreadHandler = new EventHandler(mainLooper);   
                 msgobj = "NoLooperThread has no looper and handleMessage function executed in main thread!";    
             } else{   
                 mNoLooperThreadHandler = new EventHandler(myLooper);   
                 msgobj = "This is from NoLooperThread self and handleMessage function executed in NoLooperThread!";    
             }   
             mNoLooperThreadHandler.removeMessages(0);   
             if(bpostRunnable == false){   
             //send message to main thread   
                 Message msg = mNoLooperThreadHandler.obtainMessage(2, 1, 1, msgobj);   
                 mNoLooperThreadHandler.sendMessage(msg);   
                 Log.e(TAG, "NoLooperThread id:--------+>" + this.getId());    
             }else{   
                 //下面new出来的实现了Runnable接口的对象中run函数是在Main Thread中执行，不是在NoLooperThread中执行 记得 null == myLooper么   
                 //注意Runnable是一个接口，它里面的run函数被执行时不会再新建一个线程     
                 //您可以在run上加断点然后在eclipse调试中看它在哪个线程中执行     
                 mNoLooperThreadHandler.post(new Runnable(){   
                     public void run() {   
                         // TODO Auto-generated method stub   
                         tv.setText("update UI through handler post runnalbe mechanism!");     
                         Log.e(TAG, "update UI id:--------+>" + Thread.currentThread().getId());    
                         noLooperThread.stop();     
                     }   
                        
                 });   
             }   
         }   
            
     }   
        
     class OwnLooperThread extends Thread{   
         private EventHandler mOwnLooperThreadHandler = null;   
            
         @Override  
         public void run() {   
             Looper.prepare();   
             Looper myLooper = Looper.myLooper();   
             Looper mainLooper= Looper.getMainLooper();   
                
             String msgobj;   
                
             if(null == myLooper){   
                 mOwnLooperThreadHandler = new EventHandler(mainLooper);   
                 msgobj = "OwnLooperThread has no looper and handleMessage function executed in main thread!";    
             }else{   
                 mOwnLooperThreadHandler = new EventHandler(myLooper);   
                 msgobj = "This is from OwnLooperThread self and handleMessage function executed in NoLooperThread!";   
             }   
                
             mOwnLooperThreadHandler.removeMessages(0);   
                
             //给自己发送消息   
             Message msg = mOwnLooperThreadHandler.obtainMessage(3,1,1,msgobj);   
             mOwnLooperThreadHandler.sendMessage(msg);   
             Looper.loop();   
         }   
     }   
        
     public void onClick(View v) {   
         // TODO Auto-generated method stub   
         switch(v.getId()){   
         case 101:   
             //主线程发送消息给自己   
             Looper looper = Looper.myLooper();//get the Main looper related with the main thread    
             //如果不给任何参数的话会用当前线程对应的Looper(这里就是Main Looper)为Handler里面的成员mLooper赋值   
             mHandler = new EventHandler(looper);   
             // 清除整个MessageQueue里的消息     
             mHandler.removeMessages(0);   
             String obj = "This main thread's message and received by itself!";   
                
             Message msg = mHandler.obtainMessage(1,1,1,obj);   
             // 将Message对象送入到main thread的MessageQueue里面    
             mHandler.sendMessage(msg);   
             break;   
         case 102:   
             //other线程发送消息给主线程   
             bpostRunnable = false;   
             noLooperThread = new NoLooperThread();     
             noLooperThread.start();     
             break;   
         case 103:   
              //other thread获取它自己发送的消息     
             tv.setText("please look at the error level log for other thread received message");     
             ownLooperThread = new OwnLooperThread();     
             ownLooperThread.start();     
             break;     
         case 104:         
             //other thread通过Post Runnable方式发送消息给主线程     
             bpostRunnable = true;     
             noLooperThread = new NoLooperThread();     
             noLooperThread.start();     
             break;   
         case 105:   
             //主线程发送消息给other thread     
             if(null!=mOtherThreadHandler){     
             tv.setText("please look at the error level log for other thread received message from main thread");     
             String msgObj = "message from mainThread";     
             Message mainThreadMsg = mOtherThreadHandler.obtainMessage(1, 1, 1, msgObj);     
             mOtherThreadHandler.sendMessage(mainThreadMsg);     
             }     
             break;     
         case 106:     
             finish();     
             break;     
         }   
     }   
 }  
