package com.chy.skywalking.apm.plugin.ttl.threading.wapper;


import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

public class SwRunnableWapper implements Runnable {

    private Runnable runnable;

    private ContextSnapshot contextSnapshot;

    public SwRunnableWapper(Runnable runnable, ContextSnapshot contextSnapshot) {
        this.runnable = runnable;
        this.contextSnapshot = contextSnapshot;
    }

    @Override
    public void run() {
        AbstractSpan span = ContextManager.createLocalSpan("SwRunnableWapper");
        span.setComponent(ComponentsDefine.JDK_THREADING);
        ContextManager.continued(contextSnapshot);
        runnable.run();
        ContextManager.stopSpan();
    }
}
