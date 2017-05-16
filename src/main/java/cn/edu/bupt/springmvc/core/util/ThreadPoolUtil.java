package cn.edu.bupt.springmvc.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by limingkun on 2017/5/6.
 */
public enum ThreadPoolUtil {
    POOL;
    public ExecutorService service = null;

    private ThreadPoolUtil(){
        service = Executors.newFixedThreadPool(10);
    }

}
