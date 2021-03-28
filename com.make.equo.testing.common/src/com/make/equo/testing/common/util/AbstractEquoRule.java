package com.make.equo.testing.common.util;

import java.util.Optional;

import org.eclipse.swt.widgets.Display;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.make.equo.testing.common.statements.InjectStatement;
import com.make.equo.testing.common.statements.RunInThreadStatement;

public abstract class AbstractEquoRule<T extends AbstractEquoRule<T>> implements TestRule {

	private Object testCase;

	private boolean runInNonUIThread;
	private boolean displayOwner;
	private Display display;
	
	public AbstractEquoRule(Object testCase) {
		super();
		this.testCase = testCase;
	}



	@Override
	public final Statement apply(Statement base, Description description) {
		Statement tempResult = new InjectStatement(base, testCase);
		
		Optional<Statement> optStatement = additionalStatements(tempResult);
		if(optStatement.isPresent()) {
			tempResult = optStatement.get();
		}
		if (runInNonUIThread) {
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
	public T runInNonUIThread() {
		runInNonUIThread = true;
		return (T) this;
	}
	
	public final void dispose() {
		if (runInNonUIThread) {
			flushPendingEvents();
		}
		additionalDisposes();
		disposeDisplay();
	}
	
	protected abstract void additionalDisposes();

	public void flushPendingEvents() {
		while (Display.getCurrent() != null && !Display.getCurrent().isDisposed()
				&& Display.getCurrent().readAndDispatch()) {
		}
	}

}
