package cloud.agileframework.generator;

import cloud.agileframework.generator.ui.MainDialog;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;

import java.util.Arrays;

public class AgileGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("请至少选择一张表", "提示", Messages.getInformationIcon());
            return;
        }

        if (Arrays.stream(psiElements).noneMatch(d->d instanceof DbTable)) {
            Messages.showWarningDialog("请至少选择一张表", "提示");
            return;
        }
        new MainDialog(e);
    }
}
