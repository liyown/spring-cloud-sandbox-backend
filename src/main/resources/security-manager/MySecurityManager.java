
import java.security.Permission;

public class MySecurityManager extends SecurityManager{
    @Override
    public void checkPermission(Permission perm) {
//        super.checkPermission(perm);
    }

    @Override
    public void checkWrite(String file) {
        throw new SecurityException("无权限写文件 " + file);
    }

    @Override
    public void checkDelete(String file) {
        throw new SecurityException("无权限删除文件 " + file);
    }

    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("无权限连接 " + host + ":" + port);
    }

    @Override
    public void checkAccept(String host, int port) {
        throw new SecurityException("无权限监听 " + host + ":" + port);
    }

}