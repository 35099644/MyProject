package com.llx278.exeventbus;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.llx278.exeventbus.entry.SubscribeEntry1;
import com.llx278.exeventbus.entry.SubscribeEntry2;
import com.llx278.exeventbus.entry.SubscribeEntry3;
import com.llx278.exeventbus.entry.SubscribeEntry4;
import com.llx278.exeventbus.entry.SubscribeEntry6;
import com.llx278.exeventbus.entry.SubscribeEntry7;
import com.llx278.exeventbus.entry.SubscribeEntry9;
import com.llx278.exeventbus.event.Event1;
import com.llx278.exeventbus.event.Event3;
import com.llx278.exeventbus.event.Event4;
import com.llx278.exeventbus.event.Event5;
import com.llx278.exeventbus.event.Event6;
import com.llx278.exeventbus.event.Event7;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.framework.Assert.*;

@RunWith(AndroidJUnit4.class)
public class EventBusTest {
    
    private EventBus mEventBus;
    
    public EventBusTest() {
    }

    @Before
    public void setUp() {
        mEventBus = new EventBus();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void blockReturn() throws Exception {
        SubscribeEntry6 subscribeEntry6 = new SubscribeEntry6();
        mEventBus.register(subscribeEntry6);

        Event7 event7 = new Event7("event7");
        Object returnValue = mEventBus.publish(event7, "event7", String.class.getName());
        assertNotNull(returnValue);
        assertEquals(String.class,returnValue.getClass());
        assertEquals("return_event7",returnValue);

        Event6 event6 = new Event6("event6");
        Object returnValue1 = mEventBus.publish(event6,"event6",String.class.getName());
        assertNotNull(returnValue1);
        assertEquals(String.class,returnValue1.getClass());
        assertEquals("return_event6",returnValue1);

        Event5 event5 = new Event5("event5");
        Object returnValue2 = mEventBus.publish(event5,"event5",String.class.getName());
        assertNotNull(returnValue2);
        assertEquals(String.class,returnValue1.getClass());
        assertEquals("return_event5",returnValue2);

        Event4 event4 = new Event4("event4");
        Object returnValue3 = mEventBus.publish(event4,"event4",String.class.getName());
        assertNotNull(returnValue3);
        assertEquals(String.class,returnValue1.getClass());
        assertEquals("return_event4",returnValue3);

        mEventBus.unRegister(subscribeEntry6);

        Map map = getSubscribeMap();
        assertEquals(0,map.size());
    }

    @Test
    public void postToMainThread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        final int N = 12;
        final CountDownLatch doneSignal = new CountDownLatch(N);
        SubscribeEntry1 subscribeEntry1 = new SubscribeEntry1(doneSignal);
        mEventBus.register(subscribeEntry1);
        for (int i = 0; i < N; i++) {
            if (i < 9) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Event1 event1 = new Event1("event1");
                        mEventBus.publish(event1, "event1");
                    }
                });
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Event3 event3 = new Event3("event3");
                        mEventBus.publish(event3,"event3");
                    }
                });
            }
        }
        doneSignal.await();
        mEventBus.unRegister(subscribeEntry1);
        executor.shutdown();
        executor.awaitTermination(5,TimeUnit.SECONDS);
        Log.d("main", "done");
    }


    //模拟其他线程发送事件，此事件被多个订阅者所订阅，所有订阅了此事件的订阅者都应该接收到了事件。
    @Test
    public void publicEventToSubscriber() throws Exception {
        final int n = 3;
        final CountDownLatch doneSignal = new CountDownLatch(n);
        SubscribeEntry1 subscribeEntry1 = new SubscribeEntry1(doneSignal);
        SubscribeEntry2 subscribeEntry2 = new SubscribeEntry2(doneSignal);
        mEventBus.register(subscribeEntry1);
        mEventBus.register(subscribeEntry2);
        Event3 event3 = new Event3("event3");
        mEventBus.publish(event3, "event3");
        doneSignal.await();
        mEventBus.unRegister(subscribeEntry1);
        mEventBus.unRegister(subscribeEntry2);
    }

    @Test
    public void subscribeUnSubscribe() throws Exception {

        SubscribeEntry3 subscribeEntry3 = new SubscribeEntry3(null);
        SubscribeEntry4 subscribeEntry4 = new SubscribeEntry4(null);
        List<Event> register3 = mEventBus.register(subscribeEntry3);
        assertEquals(2,register3.size());
        List<Event> register4 = mEventBus.register(subscribeEntry4);
        assertEquals(2,register4.size());

        Map subscribeMap1 = getSubscribeMap();
        assertEquals(4, subscribeMap1.size());
        List<Event> unregister3 = mEventBus.unRegister(subscribeEntry3);
        assertEquals(1,unregister3.size());
        Map subscribeMap2 = getSubscribeMap();
        assertEquals(3, subscribeMap2.size());
        List<Event> unregister4 = mEventBus.unRegister(subscribeEntry4);
        assertEquals(3,unregister4.size());
        Map subscribeMap3 = getSubscribeMap();
        assertEquals(0, subscribeMap3.size());
    }

    @Test
    public void repeatSubscribe() throws Exception {
        SubscribeEntry3 subscribeEntry3 = new SubscribeEntry3(null);
        List<Event> register3 = mEventBus.register(subscribeEntry3);
        assertEquals(2,register3.size());
        Map subMap = getSubscribeMap();
        assertEquals(2, subMap.size());
        List<Event> register31 = mEventBus.register(subscribeEntry3);
        assertEquals(0,register31.size());
        assertEquals(2, subMap.size());
        mEventBus.unRegister(subscribeEntry3);
    }

    @Test
    public void remoteSubscribe() throws Exception {
        SubscribeEntry7 subscribeEntry7 = new SubscribeEntry7(null);
        List<Event> register3 = mEventBus.register(subscribeEntry7);
        assertEquals(4,register3.size());
        assertEquals(true,register3.get(0).isRemote());
        assertEquals(true,register3.get(1).isRemote());
        assertEquals(true,register3.get(2).isRemote());
        assertEquals(true,register3.get(3).isRemote());
        mEventBus.unRegister(subscribeEntry7);
    }

    @Test
    public void query() {
        SubscribeEntry3 subscribeEntry3 = new SubscribeEntry3(null);
        mEventBus.register(subscribeEntry3);
        List<Event> query = mEventBus.query();
        assertEquals(2,query.size());
        mEventBus.unRegister(subscribeEntry3);
    }

    @Test
    public void concurrentSubscribeUnSubscribe() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(5);

        final CountDownLatch done = new CountDownLatch(4);
        final CountDownLatch done1 = new CountDownLatch(4);
        final List<Object> tempObj = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final Object o = createSubscribeEntry(i);
            tempObj.add(o);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mEventBus.register(o);
                        done.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        done.await();

        Map subMap = getSubscribeMap();
        assertEquals(6,subMap.size());
        for (final Object o : tempObj) {
            executor.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        mEventBus.unRegister(o);
                        done1.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        done1.await();

        Map subMap1 = getSubscribeMap();
        assertEquals(0,subMap1.size());
    }

    /**
     * 并发测试
     */
    @Test
    public void concurrentSubscribeUnSubscribeAndPost() throws Exception {

        final CountDownLatch doneSignal = new CountDownLatch(3);
        final ExecutorService executor = Executors.newCachedThreadPool();
        final List<Object> temp = Collections.synchronizedList(new ArrayList<Object>());
        final long timeout = 1000 * 30;
        // 订阅线程
        new Thread() {
            @Override
            public void run() {

                long endTime = SystemClock.uptimeMillis() + timeout;
                while (SystemClock.uptimeMillis() < endTime) {
                    try {
                        sleep(500);
                        Random random = new Random(SystemClock.uptimeMillis());
                        int i = random.nextInt(4) + 1;
                        final Object o = createSubscribeEntry(i);
                        temp.add(o);
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                mEventBus.register(o);
                            }
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("main", "register - done");
                }
                doneSignal.countDown();
            }
        }.start();

        // 解除订阅线程
        new Thread() {
            @Override
            public void run() {
                long endTime = SystemClock.uptimeMillis() + timeout;
                while (SystemClock.uptimeMillis() < endTime) {
                    try {
                        sleep(1000);
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                int size = temp.size();
                                Random random = new Random(SystemClock.uptimeMillis());
                                int i = random.nextInt(size);
                                Object o = temp.get(i);
                                mEventBus.unRegister(o);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                doneSignal.countDown();
            }
        }.start();

        // 发布事件线程
        new Thread() {
            @Override
            public void run() {
                super.run();

                long endTime = SystemClock.uptimeMillis() + timeout;
                while (SystemClock.uptimeMillis() < endTime) {
                    try {
                        sleep(200);
                        Random random = new Random(SystemClock.uptimeMillis());
                        int i = random.nextInt(6) + 1;
                        final Object o = createEvent(i);
                        final String tag = "event" + i;
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                mEventBus.publish(o, tag);
                            }
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                doneSignal.countDown();
            }
        }.start();

        doneSignal.await();
        executor.shutdown();
        if (!executor.awaitTermination(1000 * 5, TimeUnit.MILLISECONDS)) {
            throw new RuntimeException("Timeout!!");
        }
    }

    private Object createSubscribeEntry(int index) throws Exception {
        String className = "com.llx278.exeventbus.entry.SubscribeEntry" + index;
        Class<?> aClass = Class.forName(className);
        return aClass.newInstance();
    }

    private Object createEvent(int index) throws Exception {
        String className = "com.llx278.exeventbus.event.Event" + index;
        Class<?> aClass = Class.forName(className);
        Constructor<?> constructor = aClass.getConstructor(String.class);
        return constructor.newInstance("event" + index);
    }

    private Map getSubscribeMap() throws Exception {
        EventBus aDefault = mEventBus;
        return aDefault.getDefaultMap();
    }
}
