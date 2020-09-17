package com.kablemonck.idea.plugins.gittag;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kable Monck (kable.monck@fincleartech.com.au)
 */
@State(
        name = "GitTagPanelConfiguration",
        storages = @Storage(StoragePathMacros.WORKSPACE_FILE)
)
public class GitTagPanelConfiguration implements PersistentStateComponent<GitTagPanelConfiguration> {

    public String TAG_NAME = "";

    public static GitTagPanelConfiguration getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, GitTagPanelConfiguration.class);
    }

    @Nullable
    @Override
    public GitTagPanelConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull GitTagPanelConfiguration configuration) {
        XmlSerializerUtil.copyBean(configuration, this);
    }
}
