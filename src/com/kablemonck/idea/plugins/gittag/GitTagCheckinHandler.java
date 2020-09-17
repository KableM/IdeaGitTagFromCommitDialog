package com.kablemonck.idea.plugins.gittag;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsNotifier;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitCommit;
import git4idea.GitUtil;
import git4idea.commands.Git;
import git4idea.commands.GitCommandResult;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Kable Monck (kable.monck@fincleartech.com.au)
 */
public class GitTagCheckinHandler extends CheckinHandler {

    private final CheckinProjectPanel panel;
    private final GitTagPanel gitTagPanel;
    private final Project project;
    private Date minimumCommitDate;

    public GitTagCheckinHandler(CheckinProjectPanel panel) {
        this.panel = panel;
        project = panel.getProject();
        gitTagPanel = new GitTagPanel(project);
    }

    @Nullable
    @Override
    public RefreshableOnComponent getAfterCheckinConfigurationPanel(Disposable parentDisposable) {
        return gitTagPanel;
    }

    @Override
    public ReturnResult beforeCheckin() {
        if (gitTagPanel.isTagCommit()) {
            minimumCommitDate = new Date();
        }
        return super.beforeCheckin();
    }

    @Override
    public void checkinSuccessful() {
        if (gitTagPanel.isTagCommit()) {
            String tagName = gitTagPanel.getTagName();
            String commitMessage = panel.getCommitMessage();

            Collection<VirtualFile> roots = panel.getRoots();
            List<String> errors = new ArrayList<>();
            List<String> createdTags = new ArrayList<>();
            for (VirtualFile root : roots) {
                GitRepository repository = GitRepositoryManager.getInstance(project).getRepositoryForRootQuick(root);
                GitCommit targetCommit = getTargetCommit(root, commitMessage);
                if (targetCommit != null) {
                    String reference = targetCommit.getId().asString();

                    Git git = Git.getInstance();
                    GitCommandResult tagResult = git.createNewTag(repository, tagName, null, reference);
                    if (tagResult.success()) {
                        createdTags.add(tagName + GitUtil.mention(repository));
                    } else {
                        errors.add(tagName + GitUtil.mention(repository) + ": " + tagResult.getErrorOutputAsHtmlString());
                    }
                } else {
                    errors.add(tagName + GitUtil.mention(repository) + ": Unable to locate commit");
                }
            }

            if (createdTags.size() > 0) {
                String s = createdTags.size() > 1 ? "s" : "";
                notifySuccess("Created tag" + s, String.join("<br/>", createdTags));
            }
            if (errors.size() > 0) {
                String s = errors.size() > 1 ? "s" : "";
                notifyError("Couldn't create tag" + s, String.join("<br/>", errors));
            }
        }
    }

    @Nullable
    private GitCommit getTargetCommit(VirtualFile root, String commitMessage) {
        GitCommit targetCommit;
        try {
            List<GitCommit> commits = GitHistoryUtils.history(project, root, "-10", "--after=" + minimumCommitDate.toString());
            targetCommit = commits.stream()
                    .filter(commit -> Objects.equals(commit.getFullMessage(), commitMessage))
                    .findFirst()
                    .orElse(null);
        } catch (VcsException e) {
            targetCommit = null;
        }
        return targetCommit;
    }

    private void notifySuccess(String title, String message) {
        VcsNotifier vcsNotifier = VcsNotifier.getInstance(project);
        vcsNotifier.notifySuccess(title, message);
    }

    private void notifyError(String title, String message) {
        VcsNotifier.getInstance(project).notifyError(title, message);
    }

}
