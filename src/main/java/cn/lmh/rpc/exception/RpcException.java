package cn.lmh.rpc.exception;

/**
 * @author lmhmhl
 * @date 2020/7/25 21:34
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }
}
