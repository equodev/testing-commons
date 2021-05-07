package com.equo.testing.common.util;

import java.util.Optional;

import org.junit.runners.model.Statement;

import com.equo.testing.common.statements.EclipseStatement;

public class EclipseRule extends AbstractEquoRule<EclipseRule> {

	private EclipseStatement eclipseStatement;
	private boolean withCleanWorspace;

	public EclipseRule(Object testCase) {
		super(testCase);
		this.withCleanWorspace = false;
	}

	@Override
	protected Optional<Statement> additionalStatements(Statement base) {
		eclipseStatement = new EclipseStatement(base, getDisplay(), withCleanWorspace);
		return Optional.of(eclipseStatement);
	}

	public EclipseRule withCleanWorspace() {
		this.withCleanWorspace = true;
		return this;
	}

	@Override
	protected void additionalDisposes() {
		// Nothing to dispose
	}
	
	
}
