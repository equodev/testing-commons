/****************************************************************************
**
** Copyright (C) 2021 Equo
**
** This file is part of the Equo SDK.
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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.runners.model.Statement;
import org.osgi.framework.FrameworkUtil;

/**
 * Statement that injects a context into a given object.
 */
public class InjectStatement extends Statement {

  private Statement base;
  private Object testCase;

  public InjectStatement(Statement base, Object testCase) {
    this.base = base;
    this.testCase = testCase;
  }

  @Override
  public void evaluate() throws Throwable {
    inject(testCase);
    base.evaluate();
  }

  private void inject(Object caseTest) {
    IEclipseContext eclipseContext = EclipseContextFactory
        .getServiceContext(FrameworkUtil.getBundle(caseTest.getClass()).getBundleContext());
    ContextInjectionFactory.inject(caseTest, eclipseContext);
  }

}
