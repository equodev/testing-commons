package com.equo.testing.common.statements;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.PartRenderingEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Display;
import org.junit.runners.model.Statement;

public class RunWithE4ModelStatement extends Statement {

  private final Statement baseStatement;
  private volatile Throwable throwable;
  private Future<?> future;
  private MApplication app;
  private IEclipseContext context;
  private Display display;

  public RunWithE4ModelStatement(Statement baseStatement, MApplication app, IEclipseContext context,
      Display display) {
    this.baseStatement = baseStatement;
    this.app = app;
    this.context = context;

    // To ensure we're using the same display we have in the tests to render the UI.
    context.set(Display.class, display);
    this.display = display;
  }

  @Override
  public void evaluate() throws Throwable {
    ExecutorService executorService = runInThread();
    try {
      runApplication();
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
          RunWithE4ModelStatement.this.throwable = throwable;
        }
      }
    });
    return result;
  }

  private void ensureClose() throws InterruptedException {
    display.asyncExec(() -> {
      if (future.isDone()) {
        display.dispose();
      }
    });
  }

  private void runApplication() throws Exception {
    Executors.newSingleThreadExecutor().submit(() -> {
      try {
        while (display != null && !display.isDisposed()) {
          Thread.sleep(1500);
          ensureClose();
        }
      } catch (InterruptedException e) {
        RunWithE4ModelStatement.this.throwable = e;
      }
    });
    ContextInjectionFactory.make(PartRenderingEngine.class, context);
    E4Workbench workbench = (E4Workbench) context.get(IWorkbench.class);
    workbench.createAndRunUI(app);
  }

  private void rethrowAssertionsAndErrors() throws Throwable {
    if (throwable != null) {
      throw throwable;
    }
  }

}
