package cloud.agileframework.util;

import cloud.agileframework.generator.ui.MainDialog;

import java.awt.*;

/**
 * @author 佟盟
 * 日期 2020-12-14 16:37
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class UiUtil {
    public static void centerDialog(MainDialog dialog, String title, int width, int height) {
        dialog.setTitle(title);
        dialog.setPreferredSize(new Dimension(width, height));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2);
    }
}
