package com.myrpc.utils.ZK;

import com.myrpc.Constant;
import com.myrpc.Exceptions.ZKException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZKUtil {
    public static ZooKeeper createZK() {
        String connectionString = Constant.DEFAULT_ZK_CONNECTION;
        int timeout = Constant.TIME_OUT;
        return createZK(connectionString,timeout);
    }
    public static ZooKeeper createZK(String connectionString,int timeout) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            //创建zk实例，建立连接
            final ZooKeeper zooKeeper = new ZooKeeper(connectionString, timeout, watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("客户端连接成功");
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            return zooKeeper;
        } catch (IOException | InterruptedException e) {
            log.error("Exception when create base path:", e);
            throw new ZKException();
        }
    }

    /**
     * 创建一个节点的工具方法
     * @param zooKeeper 实例
     * @param node 节点
     * @param watcher Watcher
     * @param createMode 节点类型
     * @return true:成功创建，flase:已经存在节点
     */
    public static Boolean createNode(ZooKeeper zooKeeper,ZKNode node,Watcher watcher, CreateMode createMode){
        try {
            if(zooKeeper.exists(node.getNodePath(),null) == null) {
                String result = zooKeeper.create(node.getNodePath(), node.getData(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.info("Node [{}] has been created",result);
                return true;
            }else{
                if(log.isDebugEnabled()){
                    log.info("Node[{}] has existed",node.getNodePath());
                }
                return false;
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("Failed to create Node:",e);
            throw new ZKException();
        }
    }
    public static void close(ZooKeeper zooKeeper){
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            throw new ZKException();
        }
    }
}
