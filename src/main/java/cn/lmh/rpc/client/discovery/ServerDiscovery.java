package cn.lmh.rpc.client.discovery;

import cn.lmh.rpc.common.model.Service;

import java.util.List;

/**
 *
 * 服务发现抽象类
 * @author lmhmhl
 * @date 2020/7/25 19:45
 */
public interface ServerDiscovery {

    List<Service> findServiceList(String name);
}
