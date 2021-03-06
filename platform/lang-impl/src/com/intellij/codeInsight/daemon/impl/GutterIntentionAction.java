// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.codeInsight.daemon.impl;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PriorityAction;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Dmitry Avdeev
 */
class GutterIntentionAction extends AbstractIntentionAction implements Comparable<IntentionAction>, Iconable, ShortcutProvider,
                                                                       PriorityAction {
  private final AnAction myAction;
  private final int myOrder;
  private final Icon myIcon;
  private @IntentionName String myText;

  GutterIntentionAction(AnAction action, int order, Icon icon) {
    myAction = action;
    myOrder = order;
    myIcon = icon;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    final RelativePoint relativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(editor);
    AnActionEvent event = AnActionEvent.createFromInputEvent(
      relativePoint.toMouseEvent(), ActionPlaces.INTENTION_MENU, null, ((EditorEx)editor).getDataContext());
    if (!ActionUtil.lastUpdateAndCheckDumb(myAction, event, false)) return;
    if (myAction instanceof ActionGroup && !((ActionGroup)myAction).canBePerformed(event.getDataContext())) {
      ActionGroup group = (ActionGroup)myAction;
      JBPopupFactory.getInstance().createActionGroupPopup(
        group.getTemplatePresentation().getText(), group, event.getDataContext(),
        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
        false, null, -1, null, event.getPlace())
        .showInBestPositionFor(editor);
    }
    else {
      ActionUtil.performActionDumbAwareWithCallbacks(myAction, event, event.getDataContext());
    }
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    return myText != null ? StringUtil.isNotEmpty(myText) : isAvailable(((EditorEx)editor).getDataContext());
  }

  @NotNull
  @Override
  public Priority getPriority() {
    return myAction instanceof PriorityAction ? ((PriorityAction)myAction).getPriority() : Priority.NORMAL;
  }

  boolean isAvailable(@NotNull DataContext dataContext) {
    if (myText == null) {
      AnActionEvent event = AnActionEvent.createFromDataContext(ActionPlaces.INTENTION_MENU, null, dataContext);
      ActionUtil.performDumbAwareUpdate(false, myAction, event, false);
      if (event.getPresentation().isEnabled() && event.getPresentation().isVisible()) {
        String text = event.getPresentation().getText();
        myText = text != null ? text : StringUtil.notNullize(myAction.getTemplatePresentation().getText());
      }
      else {
        myText = "";
      }
    }
    return StringUtil.isNotEmpty(myText);
  }

  @Override
  @NotNull
  public String getText() {
    return StringUtil.notNullize(myText);
  }

  @Override
  public int compareTo(@NotNull IntentionAction o) {
    if (o instanceof GutterIntentionAction) {
      return myOrder - ((GutterIntentionAction)o).myOrder;
    }
    return 0;
  }

  @Override
  public Icon getIcon(@IconFlags int flags) {
    return myIcon;
  }

  @Nullable
  @Override
  public ShortcutSet getShortcut() {
    return myAction.getShortcutSet();
  }
}
