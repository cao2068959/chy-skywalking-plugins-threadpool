package org.apache.skywalking.apm.agent.core.plugin;


public interface PulginDefinePostProcessor {

    default void postProcessBefore(AbstractClassEnhancePluginDefine pluginDefine) {

    }


    default void postProcessAfter(AbstractClassEnhancePluginDefine pluginDefine) {

    }

}
