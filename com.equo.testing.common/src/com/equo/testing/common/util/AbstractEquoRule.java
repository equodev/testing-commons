package com.equo.testing.common.util;

import java.util.Optional;

import org.eclipse.swt.widgets.Display;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.equo.testing.common.statements.InjectStatement;
import com.equo.testing.common.statements.RunInThreadStatement;

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
