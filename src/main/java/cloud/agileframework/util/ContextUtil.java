package cloud.agileframework.util;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author 佟盟
 * 日期 2020-12-17 14:43
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ContextUtil {
    /**
     * 刷新项目
     */
    public static void refreshProject(Project project) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "刷新项目 ...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Consumer<VirtualFile> refresh = (virtualFile) -> {
                    if (virtualFile != null) {
                        virtualFile.refresh(false, true);
                    }
                };
                refresh.accept(LocalFileSystem.getInstance().findFileByPath(Objects.requireNonNull(project.getBasePath())));
                refresh.accept(project.getProjectFile());
                refresh.accept(project.getWorkspaceFile());
            }
        });
    }
}
