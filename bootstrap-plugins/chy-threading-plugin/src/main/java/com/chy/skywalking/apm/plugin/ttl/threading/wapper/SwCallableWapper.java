package com.chy.skywalking.apm.plugin.ttl.threading.wapper;


import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

import java.util.concurrent.Callable;

public class SwCallableWapper implements Callable {

    private Callable callable;

    private ContextSnapshot contextSnapshot;

    public SwCallableWapper(Callable callable, ContextSnapshot contextSnapshot) {
        this.callable = callable;
        this.contextSnapshot = contextSnapshot;
    }

    @Override
    public Object call() throws Exception {
        AbstractSpan span = ContextManager.createLocalSpan("SwCallableWapper");
        span.setComponent(ComponentsDefine.JDK_THREADING);
        ContextManager.continued(contextSnapshot);
        Object call = callable.call();
        ContextManager.stopSpan();
        return call;
    }
}
