package tmp;

import io.netty.util.HashedWheelTimer;
import org.junit.Test;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class test {
    @Test
    public void test1() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS);
        System.out.println("start:" + LocalDateTime.now().format(formatter));
        hashedWheelTimer.newTimeout(timeout -> {
            System.out.println("task :" + LocalDateTime.now().format(formatter));
        }, 3, TimeUnit.SECONDS);
        Thread.sleep(5000);
    }

    @Test
    public void test2() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS);
        System.out.println("start:" + LocalDateTime.now().format(formatter));
        hashedWheelTimer.newTimeout(timeout -> {
            Thread.sleep(3000);
            System.out.println("task1:" + LocalDateTime.now().format(formatter));
        }, 3, TimeUnit.SECONDS);
        hashedWheelTimer.newTimeout(timeout -> System.out.println("task2:" + LocalDateTime.now().format(
                formatter)), 4, TimeUnit.SECONDS);
        Thread.sleep(10000);
    }

    public static void main(String[] args) throws Exception{
        String os = System.getProperties().getProperty("os.name").toLowerCase().startsWith("win")?"INT":"USER2";
        Signal signal = new Signal(os);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("ShutdownHook execute start...");
                System.out.print("Netty NioEventLoopGroup shutdownGracefully...");
                try {
                    TimeUnit.SECONDS.sleep(10);// 模拟应用进程退出前的处理操作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ShutdownHook execute end...");
                System.out.println("Sytem shutdown over, the cost time is 10000MS");
            }
        };
        Signal.handle(signal, new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                Thread t = new Thread(runnable,"ShutdownHook-Thread");
                Runtime.getRuntime().addShutdownHook(t);
            }
        });

        for(;;){
            Thread.sleep(1000l);
        }

    }
}
