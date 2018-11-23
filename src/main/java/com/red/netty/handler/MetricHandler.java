package com.red.netty.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.red.util.Constants.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class MetricHandler extends ChannelInboundHandlerAdapter {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONNECTION = "Connection";
    private static final String KEEP_ALIVE = "keep-alive";
    private static final String METRICS = "metrics";
    private static final String METRICS_ERR_MSG = "NettyRPC nettyrpc.jmx.invoke.metrics attribute is closed!";

    private final static String TD_BEGIN = "<td>";
    private final static String TD_END = "</td>";
    private final static String TR_BEGIN = "<tr>";
    private final static String TR_END = "</tr>";
    private final static String BR = "</br>";
    private final static String TABLE_BEGIN = "<html><body><div class=\"table-container\"><table border=\"1\"><tr><th>模块名称</th><th>方法名称</th><th>调用次数</th><th>调用成功次数</th><th>调用失败次数</th><th>被过滤次数</th><th>方法耗时（毫秒）</th><th>方法最大耗时（毫秒）</th><th>方法最小耗时（毫秒）</th><th>方法耗时区间分布</th><th>最后一次失败时间</th><th>最后一次失败堆栈明细</th></tr>";
    private final static String TABLE_END = "</table></body></html>";
    private final static String SUB_TABLE_BEGIN = "<table border=\"1\">";
    private final static String SUB_TABLE_END = "</table>";
    private final static String JMX_METRICS_ATTR = "ModuleMetricsVisitor";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            byte[] content = buildResponseMsg(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
            response.headers().set(CONTENT_TYPE, "text/html");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
    }

    private byte[] buildResponseMsg(HttpRequest req) {
        byte[] content = null;
        boolean metrics = (req.getUri().indexOf(METRICS) != -1);
        if (SYSTEM_PROPERTY_JMX_METRICS_SUPPORT && metrics) {
            try {
                content = buildModuleMetrics().getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
//        } else if (!SYSTEM_PROPERTY_JMX_METRICS_SUPPORT && metrics) {
//            content = METRICS_ERR_MSG.getBytes();
//        } else {
//            AbilityDetailProvider provider = new AbilityDetailProvider();
//            content = provider.listAbilityDetail(true).toString().getBytes();
//        }
        return content;
    }
    public final static String MBEAN_NAME = "com.newlandframework.rpc:type=ModuleMetricsHandler";
    public static final long DEFAULT_INVOKE_MIN_TIMESPAN = 3600 * 1000L;
    private String buildHistogram(CompositeData data) {
        CompositeDataSupport histogram = (CompositeDataSupport) (data.get("histogram"));
        long[] ranges = (long[]) (histogram.get("ranges"));
        long[] invokeHistogram = (long[]) (data.get("invokeHistogram"));
        StringBuilder distribute = new StringBuilder();
        distribute.append(SUB_TABLE_BEGIN);
        int i = 0;
        for (; i < ranges.length; i++) {
            distribute.append(TR_BEGIN + TD_BEGIN + ((i == 0) ? i : ranges[i - 1]) + "~" + ranges[i] + "毫秒的笔数：" + invokeHistogram[i] + BR + TD_END + TR_END);
        }
        distribute.append(TR_BEGIN + TD_BEGIN + "大于等于" + ranges[i - 1] + "毫秒的笔数：" + invokeHistogram[i] + BR + TD_END + TR_END);
        distribute.append(SUB_TABLE_END);
        return distribute.toString();
    }
    public String buildModuleMetrics() {
        StringBuilder metrics = new StringBuilder();

        metrics.append(TABLE_BEGIN);
        ObjectName name = null;
        try {
            name = new ObjectName(MBEAN_NAME);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

       // try {
            Object obj = new Object();// = connection.getAttribute(name, JMX_METRICS_ATTR);
            if (obj instanceof CompositeData[]) {
                for (CompositeData compositeData : (CompositeData[]) obj) {
                    CompositeData data = (CompositeData) compositeData;
                    String moduleName = (String) (data.get("moduleName"));
                    String methodName = (String) (data.get("methodName"));
                    long invokeCount = (Long) (data.get("invokeCount"));
                    long invokeSuccCount = (Long) (data.get("invokeSuccCount"));
                    long invokeFailCount = (Long) (data.get("invokeFailCount"));
                    long invokeFilterCount = (Long) (data.get("invokeFilterCount"));
                    long invokeTimespan = (Long) (data.get("invokeTimespan"));
                    long invokeMinTimespan = ((Long) (data.get("invokeMinTimespan"))).equals(Long.valueOf(DEFAULT_INVOKE_MIN_TIMESPAN)) ? Long.valueOf(0L) : (Long) (data.get("invokeMinTimespan"));
                    long invokeMaxTimespan = (Long) (data.get("invokeMaxTimespan"));
                    String lastStackTraceDetail = (String) (data.get("lastStackTraceDetail"));
                    String lastErrorTime = (String) (data.get("lastErrorTime"));
                    String distribute = buildHistogram(data);
                    metrics.append(TR_BEGIN);
                    metrics.append(TD_BEGIN + moduleName + TD_END);
                    metrics.append(TD_BEGIN + methodName + TD_END);
                    metrics.append(TD_BEGIN + invokeCount + TD_END);
                    metrics.append(TD_BEGIN + invokeSuccCount + TD_END);
                    metrics.append(TD_BEGIN + invokeFailCount + TD_END);
                    metrics.append(TD_BEGIN + invokeFilterCount + TD_END);
                    metrics.append(TD_BEGIN + invokeTimespan + TD_END);
                    metrics.append(TD_BEGIN + invokeMaxTimespan + TD_END);
                    metrics.append(TD_BEGIN + invokeMinTimespan + TD_END);
                    metrics.append(TD_BEGIN + distribute + TD_END);
                    metrics.append(TD_BEGIN + (lastErrorTime != null ? lastErrorTime : "") + TD_END);
                    metrics.append(TD_BEGIN + lastStackTraceDetail + TD_END);
                    metrics.append(TR_END);
                }
            }
            metrics.append(TABLE_END);
        //}
//        catch (MBeanException e) {
//            e.printStackTrace();
//        } catch (AttributeNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstanceNotFoundException e) {
//            e.printStackTrace();
//        } catch (ReflectionException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return metrics.toString();
    }


}
