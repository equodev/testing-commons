package com.make.equo.testing.common.test;

import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.JavaProjectWizard;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaPerspective;
import org.eclipse.swt.widgets.Display;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.make.equo.testing.common.statements.EclipseStatement;
import com.make.equo.testing.common.util.EclipseRule;

public class EclipseRuleTest {
	
	@Rule 
	public TestName name = new TestName();
	
	@Rule
	public EclipseRule eclipseRule = new EclipseRule(this).runInNonUIThread().withCleanWorspace();
	
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
