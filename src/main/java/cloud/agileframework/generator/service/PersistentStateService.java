package cloud.agileframework.generator.service;

import cloud.agileframework.generator.model.GeneratorProperties;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 代码生成器项目服务
 * @author mydeathtrial
 */
@State(name = "AgileGeneratorService", storages = {@Storage("agile-generator-config.xml")})
public class PersistentStateService implements PersistentStateComponent<PersistentStateService> {
    public static PersistentStateService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, PersistentStateService.class);
    }

    @Override
    public @Nullable PersistentStateService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PersistentStateService persistentStateService) {
        XmlSerializerUtil.copyBean(persistentStateService, this);
    }

    private Map<String, GeneratorProperties> config;

    public Map<String, GeneratorProperties> getConfig() {
        return config;
    }

    public PersistentStateService setConfig(Map<String, GeneratorProperties> config) {
        this.config = config;
        return this;
    }
}
