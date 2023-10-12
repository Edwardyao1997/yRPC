package com.myrpc;

import com.myrpc.utils.ZK.ZKNode;
import com.myrpc.utils.ZK.ZKUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.util.List;

@Slf4j
/**
 * 注册中心的管理页面
 */
public class Application {
    public static void main(String[] args) {
        //创建基础目录


            ZooKeeper zooKeeper = ZKUtil.createZK();
            //定义节点和数据
            String basePath = "/myrpc-metadata";
            String providerPath = basePath+ "/providers";
            String consumerPath = basePath+ "/consumers";
            ZKNode baseNode = new ZKNode(basePath,null);
            ZKNode providerNode = new ZKNode(providerPath,null);
            ZKNode consumerNode = new ZKNode(consumerPath,null);
            //创建节点
            List.of(baseNode,providerNode,consumerNode).forEach(node -> {
               ZKUtil.createNode(zooKeeper,node,null,CreateMode.PERSISTENT);
            });
            ZKUtil.close(zooKeeper);

    }
}
