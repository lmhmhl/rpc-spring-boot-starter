package cn.lmh.rpc.client.net;

import cn.lmh.rpc.common.model.RpcRequest;
import cn.lmh.rpc.common.model.RpcResponse;
import cn.lmh.rpc.common.model.Service;
import cn.lmh.rpc.common.protocol.MessageProtocol;

/**
 *
 * 网络请求客户端，定义请求规范
 * @author lmhmhl
 * @date 2020/7/25 20:11
 *
 */
public interface NetClient {

    byte[] sendRequest(byte[] data, Service service) throws InterruptedException;

    RpcResponse sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol);
}
