package com.kablemonck.idea.plugins.gittag;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;

import javax.swing.*;

/**
 * @author Kable Monck (kable.monck@fincleartech.com.au)
 */
public class GitTagPanel implements RefreshableOnComponent {

    private final GitTagPanelConfiguration configuration;
    private JPanel myPanel;
    private JCheckBox tagCommitCheckBox;
    private JTextField tagNameTextField;

    public GitTagPanel(Project project) {
        configuration = GitTagPanelConfiguration.getInstance(project);
        tagCommitCheckBox.addActionListener(e -> updateEnabled());
        updateEnabled();
    }

    private void updateEnabled() {
        boolean selected = tagCommitCheckBox.isSelected();
        tagNameTextField.setEnabled(selected);
        if (selected) {
            tagNameTextField.requestFocusInWindow();
            tagNameTextField.selectAll();
        }
    }

    public boolean isTagCommit() {
        return tagCommitCheckBox.isSelected();
    }

    public String getTagName() {
        return tagNameTextField.getText();
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void refresh() {
        tagNameTextField.setText(configuration.TAG_NAME);
    }

    @Override
    public void saveState() {
        configuration.TAG_NAME = getTagName();
    }

    @Override
    public void restoreState() {
        refresh();
    }
}
