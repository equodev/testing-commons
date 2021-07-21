/****************************************************************************
**
** Copyright (C) 2021 Equo
**
** This file is part of Equo Framework.
**
** Commercial License Usage
** Licensees holding valid commercial Equo licenses may use this file in
** accordance with the commercial license agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Equo. For licensing terms
** and conditions see https://www.equoplatform.com/terms.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3 as published by the Free Software
** Foundation. Please review the following
** information to ensure the GNU General Public License requirements will
** be met: https://www.gnu.org/licenses/gpl-3.0.html.
**
****************************************************************************/

package com.equo.testing.common.util;

import java.util.Optional;

import org.eclipse.swt.widgets.Display;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.equo.testing.common.statements.InjectStatement;
import com.equo.testing.common.statements.RunInThreadStatement;

/**
 * An abstraction for all Equo rules.
 */
public abstract class AbstractEquoRule<T extends AbstractEquoRule<T>> implements TestRule {

  private Object testCase;

  private boolean runInNonUiThread;
  private boolean displayOwner;
  private Display display;

  /**
   * Parameterized constructor.
   */
  public AbstractEquoRule(Object testCase) {
    super();
    this.testCase = testCase;
  }

  @Override
  public final Statement apply(Statement base, Description description) {
    Statement tempResult = new InjectStatement(base, testCase);

    Optional<Statement> optStatement = additionalStatements(tempResult);
    if (optStatement.isPresent()) {
      tempResult = optStatement.get();
    }
    if (runInNonUiThread) {
      tempResult = new RunInThreadStatement(tempResult, getDisplay());
    }

    final Statement result = tempResult;
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          result.evaluate();
        } finally {
          dispose();
        }
      }
    };
  }

  protected abstract Optional<Statement> additionalStatements(Statement base);

  /**
   * Lazy initializes display attribute and returns it.
   * @return display associated with this rule.
   */
  public Display getDisplay() {
    if (display == null) {
      displayOwner = Display.getCurrent() == null;
      display = Display.getDefault();
    }
    return display;
  }

  private void disposeDisplay() {
    if (display != null && displayOwner) {
      if (!display.isDisposed()) {
        display.dispose();
      }
      display = null;
    }
  }

  @SuppressWarnings("unchecked")
  public T runInNonUiThread() {
    runInNonUiThread = true;
    return (T) this;
  }

  /**
   * Disposes the display previously flushing pending events.
   */
  public final void dispose() {
    if (runInNonUiThread) {
      flushPendingEvents();
    }
    additionalDisposes();
    // disposeDisplay();
  }

  protected abstract void additionalDisposes();

  /** 
   * Flushes pending events in current display.
   */
  public void flushPendingEvents() {
    while (Display.getCurrent() != null && !Display.getCurrent().isDisposed()
        && Display.getCurrent().readAndDispatch()) {
    }
  }

}
