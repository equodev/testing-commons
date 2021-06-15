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
