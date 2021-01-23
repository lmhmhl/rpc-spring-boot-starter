package cn.lmh.rpc.config;

import cn.lmh.rpc.annotation.LoadBalanceAno;
import cn.lmh.rpc.annotation.MessageProtocolAno;
import cn.lmh.rpc.client.balance.LoadBalance;
import cn.lmh.rpc.client.discovery.ZookeeperServerDiscovery;
import cn.lmh.rpc.client.net.ClientProxyFactory;
import cn.lmh.rpc.client.net.NettyNetClient;
import cn.lmh.rpc.common.protocol.MessageProtocol;
import cn.lmh.rpc.exception.RpcException;
import cn.lmh.rpc.properties.RpcConfig;
import cn.lmh.rpc.server.NettyRpcServer;
import cn.lmh.rpc.server.RequestHandler;
import cn.lmh.rpc.server.RpcServer;
import cn.lmh.rpc.server.register.DefaultRpcProcessor;
import cn.lmh.rpc.server.register.ServerRegister;
import cn.lmh.rpc.server.register.ZookeeperServerRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 注入需要的bean
 *
 * @author lmhmhl
 * @date 2020/7/25 19:43
 */
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcAutoConfiguration {


    @Bean
    public RpcConfig rpcConfig() {
        return new RpcConfig();
    }

    @Bean
    public ServerRegister serverRegister(@Autowired RpcConfig rpcConfig) {
        return new ZookeeperServerRegister(
                rpcConfig.getRegisterAddress(),
                rpcConfig.getServerPort(),
                rpcConfig.getProtocol(),
                rpcConfig.getWeight());
    }

    @Bean
    public RequestHandler requestHandler(@Autowired ServerRegister serverRegister,
                                         @Autowired RpcConfig rpcConfig) {
        return new RequestHandler(getMessageProtocol(rpcConfig.getProtocol()), serverRegister);
    }

    @Bean
    public RpcServer rpcServer(@Autowired RequestHandler requestHandler,
                               @Autowired RpcConfig rpcConfig) {
        return new NettyRpcServer(rpcConfig.getServerPort(), rpcConfig.getProtocol(), requestHandler);
    }


    @Bean
    public ClientProxyFactory proxyFactory(@Autowired RpcConfig rpcConfig) {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();
        // 设置服务发现着
        clientProxyFactory.setServerDiscovery(new ZookeeperServerDiscovery(rpcConfig.getRegisterAddress()));

        // 设置支持的协议
        Map<String, MessageProtocol> supportMessageProtocols = buildSupportMessageProtocols();
        clientProxyFactory.setSupportMessageProtocols(supportMessageProtocols);
        // 设置负载均衡算法
        LoadBalance loadBalance = getLoadBalance(rpcConfig.getLoadBalance());
        clientProxyFactory.setLoadBalance(loadBalance);
        // 设置网络层实现
        clientProxyFactory.setNetClient(new NettyNetClient());

        return clientProxyFactory;
    }

    private MessageProtocol getMessageProtocol(String name) {
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        Iterator<MessageProtocol> iterator = loader.iterator();
        while (iterator.hasNext()) {
            MessageProtocol messageProtocol = iterator.next();
            MessageProtocolAno ano = messageProtocol.getClass().getAnnotation(MessageProtocolAno.class);
            Assert.notNull(ano, "message protocol name can not be empty!");
            if (name.equals(ano.value())) {
                return messageProtocol;
            }
        }
        throw new RpcException("invalid message protocol config!");
    }


    private Map<String, MessageProtocol> buildSupportMessageProtocols() {
        Map<String, MessageProtocol> supportMessageProtocols = new HashMap<>();
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        Iterator<MessageProtocol> iterator = loader.iterator();
        while (iterator.hasNext()) {
            MessageProtocol messageProtocol = iterator.next();
            MessageProtocolAno ano = messageProtocol.getClass().getAnnotation(MessageProtocolAno.class);
            Assert.notNull(ano, "message protocol name can not be empty!");
            supportMessageProtocols.put(ano.value(), messageProtocol);
        }
        return supportMessageProtocols;
    }

    /**
     * 使用spi匹配符合配置的负载均衡算法
     *
     * @param name
     * @return
     */
    private LoadBalance getLoadBalance(String name) {
        ServiceLoader<LoadBalance> loader = ServiceLoader.load(LoadBalance.class);
        Iterator<LoadBalance> iterator = loader.iterator();
        while (iterator.hasNext()) {
            LoadBalance loadBalance = iterator.next();
            LoadBalanceAno ano = loadBalance.getClass().getAnnotation(LoadBalanceAno.class);
            Assert.notNull(ano, "load balance name can not be empty!");
            if (name.equals(ano.value())) {
                return loadBalance;
            }
        }
        throw new RpcException("invalid load balance config");
    }

    @Bean
    public DefaultRpcProcessor rpcProcessor(@Autowired ClientProxyFactory clientProxyFactory,
                                            @Autowired ServerRegister serverRegister,
                                            @Autowired RpcServer rpcServer) {
        return new DefaultRpcProcessor(clientProxyFactory, serverRegister, rpcServer);
    }


}
