package com.kablemonck.idea.plugins.gittag;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.VcsCheckinHandlerFactory;
import git4idea.GitVcs;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kable Monck (kable.monck@fincleartech.com.au)
 */
public class GitTagCheckinHandlerFactory extends VcsCheckinHandlerFactory {

    protected GitTagCheckinHandlerFactory() {
        super(GitVcs.getKey());
    }

    @NotNull
    @Override
    protected CheckinHandler createVcsHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        return new GitTagCheckinHandler(panel);
    }
}
