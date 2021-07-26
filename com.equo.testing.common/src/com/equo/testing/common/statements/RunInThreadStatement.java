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

package com.equo.testing.common.statements;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Display;
import org.junit.runners.model.Statement;

/**
 * Statement to run test in current thread. Manages the display events to be
 * read and dispatched.
 */
public class RunInThreadStatement extends Statement {

  private final Statement baseStatement;
  private Future<?> future;
  private volatile Throwable throwable;
  private Display display;

  public RunInThreadStatement(Statement baseStatement, Display display) {
    this.baseStatement = baseStatement;
    this.display = display;
  }

  @Override
  public void evaluate() throws Throwable {
    ExecutorService executorService = runInThread();
    try {
      waitTillFinished();
    } finally {
      executorService.shutdown();
    }
    rethrowAssertionsAndErrors();
  }

  private ExecutorService runInThread() {
    ExecutorService result = Executors.newSingleThreadExecutor();
    future = result.submit(new Runnable() {
      @Override
      public void run() {
        try {
          baseStatement.evaluate();
        } catch (Throwable throwable) {
          RunInThreadStatement.this.throwable = throwable;
        }
      }
    });
    return result;
  }

  private void waitTillFinished() {
    while (!future.isDone()) {
      if (!display.isDisposed() && !display.readAndDispatch()) {
        display.sleep();
      }
    }
  }

  private void rethrowAssertionsAndErrors() throws Throwable {
    if (throwable != null) {
      throw throwable;
    }
  }
}
