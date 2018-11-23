#### 1.服务端启动后无法连接

##### 修复：
```
new ChannelInitializer<NioServerSocketChannel>() {
    protected void initChannel(NioServerSocketChannel channel) throws Exception {
        channel.pipeline().addLast(new ServerHandler());
    }
}
```
改为
```
new ChannelInitializer<SocketChannel>() {
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new ServerHandler());
    }
}
```

##### 原因：
实际类型为io.netty.channel.socket.nio.NioSocketChannel
netty server 的 childHandler方法会在服务端成功接收一个新的连接后创建，此时创建报错，导致连接中断，但服务端新的线程NioEventLoop仍在运行

NioEventLoop的accept事件触发AbstractNioMessageChannel后通过channelRead事件执行到ServerBootstrapAcceptor中,
```
child.pipeline().addLast(new ChannelHandler[]{this.childHandler});
```
然后在新的IO线程启动时完成实际的添加操作

#### 2.服务端没有接收到客户端传送过来的对象以及服务端收到消息后没有触发ServerHandler的channelRead方法

##### 修复：
在客户端服务端添加编解码器

##### 原因：
在客户端调用write传输对象时，会从tail端开始寻找ChannelOutboundHandler，由于没有编码器，直接找到HeadContext，
在HeadContext的写方法中，判断对象没有被转成ByteBuf就抛出异常


#### 3.在Cglib代理类的intercept方法里直接封装返回了RedMessage导致类型转换错误

##### 修复：
返回原方法返回的类型

##### 原因：
cglib继承代理类，代理的方法重写原来的方法，在其中调用intercept，返回类型还应该与原方法保持一致
