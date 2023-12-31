package com.myrpc.utils.ZK;

import com.myrpc.Constant;
import com.myrpc.Exceptions.ZKException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZKUtils {
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
                    log.debug("客户端连接成功");
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

    /**
     * 判断节点是否存在
     * @param zooKeeper 实例
     * @param node 路径
     * @param watcher
     * @return true 存在 | false 不存在
     */
    public static Boolean exists(ZooKeeper zooKeeper,String node,Watcher watcher){
        try {
            return zooKeeper.exists(node,watcher) != null;
        } catch (KeeperException | InterruptedException e) {
            log.error("Exception when check Noode {} exitance",node,e);
            throw new ZKException(e);
        }
    }

    /**
     * 查询节点的子元素
     * @param zooKeeper zk实例
     * @param serviceNode 服务节点
     * @return
     */
    public static List<String> getChildren(ZooKeeper zooKeeper, String serviceNode,Watcher watcher) {
        try {
            return zooKeeper.getChildren(serviceNode, watcher);
        } catch (KeeperException | InterruptedException e) {
            log.error("获取节点{}的子元素时发生异常，",serviceNode,e);
            throw new ZKException(e);
        }
    }
}
