/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

import java.io.IOException;
// Java 2 Standard Edition packages
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import org.apromore.commons.config.ConfigBean;
// Local classes
import org.apromore.manager.client.ManagerService;
import org.apromore.service.AuthorizationService;
import org.apromore.service.EventLogService;
import org.apromore.service.FormatService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;
import lombok.Getter;
import lombok.Setter;

/**
 * Base Controller that all controllers inherit.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
@Getter
@Setter
public class BaseController extends Window {

  public static final String XPDL_2_2 = "XPDL 2.2";
  public static final String BPMN_2_0 = "BPMN 2.0";
  public static final String PNML_1_3_2 = "PNML 1.3.2";
  public static final String YAWL_2_2 = "YAWL 2.2";
  public static final String EPML_2_0 = "EPML 2.0";

  public static final String UNTITLED_PROCESS_NAME = "Untitled";
  public static final String VERSION_1_0 = "1.0";

  @WireVariable("managerClient")
  private ManagerService managerService;
  @WireVariable("eventLogService")
  private EventLogService eventLogService;
  @WireVariable
  private UserService userService;
  @WireVariable
  private SecurityService securityService;

  @WireVariable
  private AuthorizationService authorizationService;
  @WireVariable
  private WorkspaceService workspaceService;

  @WireVariable
  private ProcessService processService;

  @WireVariable
  private FormatService formatService;

  protected BaseController() {

  }

  protected void init(Component component) {
    Selectors.wireVariables(component, this, Selectors.newVariableResolvers(getClass(), null));
  }

  /** Unit testing constructor. */
  protected BaseController(ConfigBean configBean) {

  }


  public ConfigBean getConfig() {
    return eventLogService.getConfigBean();
  }

  /**
   * Turn a ZUL document into a ZK {@link Component}.
   *
   * This method requires that the ZUL document is a resource in the classpath of the calling class.
   * It's suitable for use by portal plugins. Beware that this doesn't work with ZUL in the portal's
   * src/main/webapp directory.
   *
   * @param zulPath path to a ZUL document within the calling bundle's classpath
   * @return a ZK {@link Component} constructed from the ZUL document at <var>zulPath</var>
   * @throws IllegalArgumentException if no resource can be read from <var>zulPath</var>
   */
  protected <T extends Component> T createComponent(String zulPath) {

    try {
      InputStream in = getClass().getClassLoader().getResourceAsStream(zulPath);

      return (T) Executions.createComponentsDirectly(new InputStreamReader(in), "zul", null, null);

    } catch (IOException e) {
      throw new IllegalArgumentException(zulPath + " not found", e);
    }
  }

  protected String getURL(final String nativeType) {
    String url = "";
    switch (nativeType) {
      case XPDL_2_2:
        url = "http://b3mn.org/stencilset/bpmn2.0#";
        break;
      case BPMN_2_0:
        url = "http://b3mn.org/stencilset/bpmn2.0#";
        break;
      case PNML_1_3_2:
        url = "http://b3mn.org/stencilset/petrinet#";
        break;
      case YAWL_2_2:
        url = "http://b3mn.org/stencilset/yawl2.2#";
        break;
      case EPML_2_0:
        url = "http://b3mn.org/stencilset/epc#";
        break;
      default:
    }
    return url;
  }


  public String getImportPath(final String nativeType) {
    String importPath = "";
    switch (nativeType) {
      case XPDL_2_2:
        importPath = "/" + getConfig().getSiteEditor() + "/editor/xpdlimport";
        break;
      case BPMN_2_0:
        importPath = "/" + getConfig().getSiteEditor() + "/editor/bpmnimport";
        break;
      case PNML_1_3_2:
        importPath = "/" + getConfig().getSiteEditor() + "/editor/pnmlimport";
        break;
      case YAWL_2_2:
        importPath = "/" + getConfig().getSiteEditor() + "/editor/yawlimport";
        break;
      case EPML_2_0:
        importPath = "/" + getConfig().getSiteEditor() + "/editor/epmlimport";
        break;
      default:
    }
    return importPath;
  }

  public String getExportPath(final String nativeType) {
    String exportPath = "";
    switch (nativeType) {
      case XPDL_2_2:
        exportPath = "/" + getConfig().getSiteEditor() + "/editor/xpdlexport";
        break;
      case BPMN_2_0:
        exportPath = "/" + getConfig().getSiteEditor() + "/editor/bpmnexport";
        break;
      case PNML_1_3_2:
        exportPath = "/" + getConfig().getSiteEditor() + "/editor/pnmlexport";
        break;
      case YAWL_2_2:
        exportPath = "/" + getConfig().getSiteEditor() + "/editor/yawlexport";
        break;
      case EPML_2_0:
        exportPath = "/" + getConfig().getSiteEditor() + "/editor/epmlexport";
        break;
      default:
    }
    return exportPath;
  }

  public void toggleComponentSclass(HtmlBasedComponent comp, boolean state, String stateOff,
      String stateOn) {
    if (comp == null || stateOn == null || stateOff == null) {
      return;
    }
    String sclass = Objects.requireNonNull(comp.getSclass(), "");
    if (state) {
      sclass = sclass.replaceAll(stateOff, "");
      if (!sclass.contains(stateOn)) {
        sclass = sclass + " " + stateOn;
      }
    } else {
      sclass = sclass.replaceAll(stateOn, "");
      if (!sclass.contains(stateOff)) {
        sclass = sclass + " " + stateOff;
      }
    }
    comp.setSclass(sclass);
  }

  public String processRequest(Map<String, String[]> parameterMap) {
    return "";
  }


}
