package cloud.agileframework.generator.ui;

import cloud.agileframework.generator.model.AgileTableModel;
import cloud.agileframework.generator.model.AnnotationType;
import cloud.agileframework.generator.model.GeneratorProperties;
import cloud.agileframework.generator.service.PersistentStateService;
import cloud.agileframework.util.ContextUtil;
import cloud.agileframework.util.FreemarkerUtil;
import cloud.agileframework.util.UiUtil;
import com.google.common.collect.Maps;
import com.intellij.database.model.DasTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiElement;
import com.intellij.ui.DocumentAdapter;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author mydeathtrial
 */
public class MainDialog extends JDialog {
    public static final String HISTORY_CONFIG = "history";
    /**
     * 整体面板
     */
    private JPanel contentPane;
    /**
     * 确定按钮
     */
    private JButton buttonOK;
    /**
     * 取消按钮
     */
    private JButton buttonCancel;
    private JTable columnTypeTable;
    private JButton addButton;
    private JButton delButton;

    /**
     * 已经选择的表
     */
    private PsiElement[] psiElements;
    /**
     * 所在项目
     */
    private Project project;
    /**
     * 事件
     */
    private AnActionEvent anActionEvent;

    /**
     * 配置服务
     */
    private PersistentStateService persistentStateService;

    public MainDialog(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;

        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        this.persistentStateService = PersistentStateService.getInstance(project);

        UiUtil.centerDialog(this, "Agile代码生成器", 850, 450);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // 单击十字时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        initConfig();
        pack();
        setVisible(true);
    }

    /**
     * 选择完源码包以后更新生成目录
     *
     * @return 源码包配置更改监听
     */
    @NotNull
    private DocumentAdapter onJavaSourceUrlChangeListener(FileChooserDescriptor fileChooserDescriptor) {
        return new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                entityUrl.setEnabled(true);
                entityUrl.setText(javaSourceUrl.getText());
                fileChooserDescriptor.setRoots(LocalFileSystem.getInstance().findFileByPath(javaSourceUrl.getText()));
            }
        };
    }

    private void onOK() {
        GeneratorProperties config = config();
        Set<AnnotationType> annotationTypes = config.getAnnotation();
        parseAnnotationTypes(annotationTypes, jpaCheckBox, AnnotationType.JPA);
        parseAnnotationTypes(annotationTypes, validateCheckBox, AnnotationType.VALIDATE);
        parseAnnotationTypes(annotationTypes, remarkCheckBox, AnnotationType.REMARK);
        parseAnnotationTypes(annotationTypes, lombokCheckBox, AnnotationType.LOMBOK);

        config.setEntityPrefix(entityPrefix.getText());
        config.setEntitySuffix(entitySuffix.getText());

        final String entityUrlText = entityUrl.getText();
        if (StringUtils.isEmpty(entityUrlText)) {
            Messages.showMessageDialog("生成路径不能为空", "提示", Messages.getErrorIcon());
            return;
        }
        config.setEntityUrl(entityUrlText);

        final String javaSourceUrlText = javaSourceUrl.getText();
        config.setJavaSourceUrl(javaSourceUrlText);
        if (StringUtils.isEmpty(javaSourceUrlText)) {
            Messages.showMessageDialog("必须指定源码包", "提示", Messages.getErrorIcon());
            return;
        } else if (!entityUrlText.contains(javaSourceUrlText)) {
            Messages.showMessageDialog("生成路径下未包含指定的源码包", "提示", Messages.getErrorIcon());
            return;
        }

        for (PsiElement psiElement : psiElements) {
            if (psiElement instanceof DasTable) {
                AgileTableModel table = new AgileTableModel((DasTable) psiElement, config);
                try {
                    generateEntityFile(table);
                } catch (IOException | TemplateException e) {
                    e.printStackTrace();
                }
            }
        }

        ContextUtil.refreshProject(project);
        Messages.showMessageDialog("代码已生成到指定" + config.getEntityUrl(), "提示", Messages.getInformationIcon());

        // 在此处添加代码
        dispose();
    }

    private void parseAnnotationTypes(Set<AnnotationType> annotationTypes, JCheckBox jpaCheckBox, AnnotationType jpa) {
        if (jpaCheckBox.isSelected()) {
            annotationTypes.add(jpa);
        } else {
            annotationTypes.remove(jpa);
        }
    }

    /**
     * 生成实体文件
     *
     * @param tableModel 表信息集
     * @throws IOException       异常
     * @throws TemplateException 异常
     */
    private static void generateEntityFile(AgileTableModel tableModel) throws IOException, TemplateException {
        String fileName = tableModel.getEntityName() + ".java";
        FreemarkerUtil.generatorProxy("Entity.ftl", tableModel.getUrl(), fileName, tableModel, false);
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }

    /**
     * 初始化渲染配置页面
     */
    private void initConfig() {
        GeneratorProperties config = config();
        parseColumnTypeTable(config.getColumnType());

        javaSourceUrl.getTextField().setEditable(false);
        entityUrl.getTextField().setEditable(false);

        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        fileChooserDescriptor.setRoots(LocalFileSystem.getInstance().findFileByPath(project.getBasePath()));
        if (config.getJavaSourceUrl() != null) {
            javaSourceUrl.setText(config.getJavaSourceUrl());
        }
        javaSourceUrl.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor));

        final FileChooserDescriptor fileChooserDescriptorTwo = new FileChooserDescriptor(false, true, false, false, false, false);
        if (config.getEntityUrl() == null) {
            fileChooserDescriptorTwo.setRoots(fileChooserDescriptor.getRoots());
        } else {
            entityUrl.setText(config.getEntityUrl());
            fileChooserDescriptorTwo.setRoots(LocalFileSystem.getInstance().findFileByPath(config.getJavaSourceUrl()));
        }
        entityUrl.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptorTwo));

        javaSourceUrl.getTextField().getDocument().addDocumentListener(onJavaSourceUrlChangeListener(fileChooserDescriptorTwo));


        parseField(entityPrefix, config.getEntityPrefix());
        parseField(entitySuffix, config.getEntitySuffix());


        config.getAnnotation().forEach(e -> {
            switch (e) {
                case JPA:
                    parseJCheckBox(jpaCheckBox, true);
                    break;
                case LOMBOK:
                    parseJCheckBox(lombokCheckBox, true);
                    break;
                case REMARK:
                    parseJCheckBox(remarkCheckBox, true);
                    break;
                case VALIDATE:
                    parseJCheckBox(validateCheckBox, true);
                    break;
                default:
                    break;
            }
        });

    }

    /**
     * 获取当前有效配置
     *
     * @return 配置信息
     */
    private GeneratorProperties config() {
        Map<String, GeneratorProperties> configs = persistentStateService.getConfig();
        GeneratorProperties config;
        if (configs == null) {
            configs = Maps.newHashMap();
            config = new GeneratorProperties();
            configs.put(HISTORY_CONFIG, config);
            persistentStateService.setConfig(configs);
        } else {
            config = configs.get(HISTORY_CONFIG);
        }
        return config;
    }

    private JTextField aTextField;
    private JTextField bTextField;
    private JTextField entityPrefix;
    private JTextField entitySuffix;
    private JCheckBox jpaCheckBox;
    private JCheckBox remarkCheckBox;
    private JCheckBox validateCheckBox;
    private JCheckBox lombokCheckBox;
    private JScrollPane tableScrollPane;
    private JTabbedPane tabbedPane1;
    private TextFieldWithBrowseButton javaSourceUrl;
    private TextFieldWithBrowseButton entityUrl;
    private DefaultTableModel tableModel;
    private final TextField aHidden = new TextField();
    private final TextField bHidden = new TextField();

    private void parseField(JTextField field, String text) {
        field.setText(text);
    }

    private void parseJCheckBox(JCheckBox box, boolean is) {
        box.setSelected(is);
    }

    /**
     * 处理字段映射的表格
     */
    private void parseColumnTypeTable(Map<String, Class<?>> columnType) {
        //去掉滚动面板的边框
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        //设置表头
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        String[] columnNames = {"字段类型", "Java类型"};
        tableModel.setColumnIdentifiers(columnNames);

        columnType.forEach((key, value) -> {
            String[] rowValues = {key, value.getCanonicalName()};
            tableModel.addRow(rowValues);
        });

        columnTypeTable.setModel(tableModel);
        columnTypeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        columnTypeTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (TableModelEvent.UPDATE != e.getType()) {
                    return;
                }
                int editRow = e.getFirstRow();
                if (editRow == -1) {
                    return;
                }
                Object oa = tableModel.getValueAt(editRow, 0);
                Object ob = tableModel.getValueAt(editRow, 1);

                if (!aHidden.getText().equals(oa) && validateColumnTypeKey(oa, aHidden.getText())) {
                    tableModel.setValueAt(aHidden.getText(), editRow, 0);
                    return;
                } else if (!bHidden.getText().equals(ob) && validateColumnTypeValue(ob)) {
                    tableModel.setValueAt(bHidden.getText(), editRow, 1);
                    return;
                }
                config().getColumnType().remove(aHidden.getText());
                config().getColumnType().put((String) oa, toClass((String) ob));
            }
        });


        columnTypeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = columnTypeTable.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                Object oa = tableModel.getValueAt(selectedRow, 0);
                Object ob = tableModel.getValueAt(selectedRow, 1);
                aHidden.setText(oa.toString());
                bHidden.setText(ob.toString());
            }
        });

        addButton.addActionListener(e -> {

            String[] rowValues = {aTextField.getText(), bTextField.getText()};

            if (validateColumnTypeKey(aTextField.getText(), aTextField.getText())
                    || validateColumnTypeValue(bTextField.getText())) {
                return;
            }

            tableModel.addRow(rowValues);
            aTextField.setText(null);
            bTextField.setText(null);
        });

        delButton.addActionListener(e -> {
            int[] selectedRows = columnTypeTable.getSelectedRows();
            if (selectedRows.length == 0) {
                Messages.showMessageDialog("请至少选择一条要删除的数据", "提示", Messages.getErrorIcon());
                return;
            }
            Arrays.sort(selectedRows);
            for (int i = 0; i < selectedRows.length / 2; i++) {
                int temp = selectedRows[i];
                selectedRows[i] = selectedRows[selectedRows.length - 1 - i];
                selectedRows[selectedRows.length - 1 - i] = temp;
            }
            for (int row : selectedRows) {
                tableModel.removeRow(row);
            }
        });
    }

    /**
     * 验证字段映射配置
     *
     * @param newV 字段映射的key值
     * @return 是否非法
     */
    public boolean validateColumnTypeKey(Object newV, Object oldV) {
        if (!(newV instanceof String) || StringUtils.isEmpty((String) newV)) {
            Messages.showMessageDialog("字段映射不允许为空值", "提示", Messages.getErrorIcon());
            return true;
        }
        GeneratorProperties config = config();
        if (!newV.equals(oldV) && config.getColumnType().containsKey(String.valueOf(newV))) {
            Messages.showMessageDialog(newV + "类型已经存在，不能重复添加", "提示", Messages.getErrorIcon());
            return true;
        }
        return false;
    }

    public boolean validateColumnTypeValue(Object columnTypeValue) {
        if (columnTypeValue == null) {
            Messages.showMessageDialog("字段映射不允许为空值", "提示", Messages.getErrorIcon());
            return true;
        }

        final String className = String.valueOf(columnTypeValue);
        return toClass(className) == null;
    }

    private Class<?> toClass(String className) {
        if ("byte[]".equals(className)) {
            return byte[].class;
        } else if ("byte".equals(className)) {
            return byte.class;
        } else if ("char".equals(className)) {
            return char.class;
        } else if ("int".equals(className)) {
            return int.class;
        } else if ("long".equals(className)) {
            return long.class;
        } else if ("short".equals(className)) {
            return short.class;
        } else if ("double".equals(className)) {
            return double.class;
        } else if ("float".equals(className)) {
            return float.class;
        } else if ("boolean".equals(className)) {
            return boolean.class;
        } else {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                Messages.showMessageDialog(className + "无法识别为一个JAVA类，不能准确映射", "提示", Messages.getInformationIcon());
                return null;
            }
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
