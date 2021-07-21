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

package com.equo.testing.common.test;

import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.JavaProjectWizard;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaPerspective;
import org.eclipse.swt.widgets.Display;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.equo.testing.common.statements.EclipseStatement;
import com.equo.testing.common.util.EclipseRule;

public class EclipseRuleTest {

  @Rule
  public TestName name = new TestName();

  @Rule
  public EclipseRule eclipseRule = new EclipseRule(this).runInNonUiThread().withCleanWorkspace();

  @Test
  public void ruleRunWithoutExceptionsTest() {
    System.out.println("Running Test");
    //Open Java perspective
    new JavaPerspective().open();
    //Given a project
    JavaProjectWizard projectDlg = new JavaProjectWizard();
    projectDlg.open();
    NewJavaProjectWizardPageOne projectPage = new NewJavaProjectWizardPageOne(projectDlg);
    projectPage.setProjectName("equoTestProject");
    projectDlg.finish();

    // Create dummy statement to clean workspace
    Display display = eclipseRule.getDisplay();
    EclipseStatement testStatement = new EclipseStatement(null, display, true);
    testStatement.cleanWorkspace(display);

    // Assert workspace is clean
    PackageExplorerPart packageExplorer = new PackageExplorerPart();
    assertTrue(packageExplorer.getProjects().isEmpty());
  }

  @Test
  public void cleanProjectTest() {
    System.out.println("cleanProjectTest");
    //Open Java perspective
    new JavaPerspective().open();
    //Given a project
    JavaProjectWizard projectDlg = new JavaProjectWizard();
    projectDlg.open();
    NewJavaProjectWizardPageOne projectPage = new NewJavaProjectWizardPageOne(projectDlg);
    projectPage.setProjectName("equoTestProject");
    projectDlg.finish();

    //Create dummy statement to clean workspace
    Display display = eclipseRule.getDisplay();
    EclipseStatement testStatement = new EclipseStatement(null, display, true);
    testStatement.cleanWorkspace(display);

    //Assert workspace is clean
    PackageExplorerPart packageExplorer = new PackageExplorerPart();
    assertTrue(packageExplorer.getProjects().isEmpty());

  }

}
