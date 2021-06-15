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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.equinox.internal.app.EclipseAppContainer;
import org.eclipse.equinox.internal.app.EclipseAppHandle;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.internal.framework.EquinoxConfiguration;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.eclipse.osgi.service.runnable.ApplicationLauncher;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.condition.ActiveShellExists;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.swt.widgets.Display;
import org.junit.runners.model.Statement;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationHandle;

public class EclipseStatement extends Statement {

  private Statement base;
  private Display display;
  private boolean cleanWorkspace;
  private static EclipseAppLauncher appLauncher = null;
  private static BundleContext context = null;
  private static boolean alreadyLaunch = false;

  /**
   * Parameterized constructor.
   */
  public EclipseStatement(Statement base, Display display, boolean cleanWorkspace) {
    super();
    this.base = base;
    this.display = display;
    this.cleanWorkspace = cleanWorkspace;

    Runtime.getRuntime().addShutdownHook(new Thread(() -> closeIde()));

  }

  @Override
  public void evaluate() throws Throwable {
    launchIde(display);
    alreadyLaunch = true;
    if (cleanWorkspace) {
      cleanWorkspace(display);
    }
    try {
      base.evaluate();
    } finally {
      closeIde();
    }
  }

  public void cleanWorkspace(Display display) {
    CleanWorkspaceRequirement cleanWS = new CleanWorkspaceRequirement();
    cleanWS.fulfill();
  }

  private void launchIde(Display display) {
    Map<String, String> props = new HashMap<String, String>();
    props.put("eclipse.application.launchDefault", "true");
    EclipseStarter.setInitialProperties(props);

    display.asyncExec(() -> {

      //IMPORTANT: Don't deletes the next sysout, is there to force early startup 
      // of org.eclipse.equinox.app bundle.
      System.out.println(EclipseAppContainer.class.getName());

      // create the ApplicationLauncher and register it as a service
      if (!alreadyLaunch) {
        context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

        ServiceReference<FrameworkLog> logRef = context.getServiceReference(FrameworkLog.class);
        FrameworkLog log = context.getService(logRef);
        ServiceReference<EnvironmentInfo> configRef =
            context.getServiceReference(EnvironmentInfo.class);
        EquinoxConfiguration equinoxConfig = (EquinoxConfiguration) context.getService(configRef);
        appLauncher = new EclipseAppLauncher(context, false, false, log, equinoxConfig);
        context.registerService(ApplicationLauncher.class.getName(), appLauncher, null);
      }
      // Must start the launcher AFTER service registration because this method
      // blocks and runs the application on the current thread. This method
      // will return only after the application has stopped.
      try {
        if (alreadyLaunch) {
          appLauncher.reStart(null);
        } else {
          appLauncher.start(null);
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

    });

    new WaitUntil(new ActiveShellExists(), TimePeriod.VERY_LONG);
  }

  /**
   * Closes the IDE using the application handle obtained from the eclipse context.
   */
  public void closeIde() {
    IEclipseContext eclipseContext = EclipseContextFactory
        .getServiceContext(FrameworkUtil.getBundle(this.getClass()).getBundleContext());
    if (eclipseContext.containsKey(ApplicationHandle.class)) {
      ApplicationHandle applicationHandle = eclipseContext.get(ApplicationHandle.class);
      if (applicationHandle != null) {
        ((EclipseAppHandle) applicationHandle).stop();
      }
    }
  }

}
