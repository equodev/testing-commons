package com.equo.testing.common.util;

import org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class ModelHelper {

  public static MApplication getModelApplication(String modelPath) {
    ResourceSet resourceSet = new ResourceSetImpl();
    ApplicationPackageImpl.init();
    URI uri = URI.createURI(modelPath);

    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("e4xmi",
        new E4XMIResourceFactory());
    Resource res = resourceSet.getResource(uri, true);
    MApplication app = (MApplication) res.getContents().get(0);

    return app;
  }

}
