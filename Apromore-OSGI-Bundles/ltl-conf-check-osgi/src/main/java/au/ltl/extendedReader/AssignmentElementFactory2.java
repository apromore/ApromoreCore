package au.ltl.extendedReader;

import org.processmining.plugins.declare.visualizing.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by armascer on 17/11/2017.
 */
public class AssignmentElementFactory2  extends XMLElementFactory2 {
        private static final String TAG_ASSIGNMENT = "assignment";
        private static final String TAG_ASSIGNMENT_NAME = "name";
        private static final String TAG_ASSIGNMENT_LANGUAGE = "language";
        private static final String TAG_ACTIVITY_DEFINITIONS = "activitydefinitions";
        private static final String TAG_ACTIVITY_DEFINITION = "activity";
        private static final String TAG_ACTIVITY_DEFINITION_NAME = "name";
        private static final String TAG_ACTIVITY_DEFINITION_AUTHORIZATIONS = "authorization";
        private static final String TAG_ACTIVITY_DEFINITION_DATA_MODEL = "datamodel";
        private static final String TAG_ACTIVITY_DEFINITION_DATA_DEFINITION = "data";
        private static final String TAG_ACTIVITY_DEFINITION_DATA_ELEMENT = "element";
        private static final String TAG_ACTIVITY_DEFINITION_DATA_TYPE = "type";
        private static final String TAG_CONSTRAINT_DEFINITIONS = "constraintdefinitions";
        private static final String TAG_CONSTRAINT_DEFINITION = "constraint";
        private static final String TAG_CONSTRAINT_DEFINITION_NAME = "name";
        private static final String TAG_CONSTRAINT_DEFINITION_MANDATORY = "mandatory";
        private static final String TAG_CONSTRAINT_DEFINITION_CONDITION = "condition";
        private static final String TAG_CONSTRAINT_DEFINITION_TEMPLATE = "template";
        private static final String TAG_CONSTRAINT_DEFINITION_PARAMETERS = "constraintparameters";
        private static final String TAG_CONSTRAINT_DEFINITION_PARAMETER = "parameter";
        private static final String TAG_CONSTRAINT_DEFINITION_PARAMETER_LTL = "templateparameter";
        private static final String TAG_CONSTRAINT_DEFINITION_PARAMETER_BRANCHES = "branches";
        private static final String TAG_CONSTRAINT_DEFINITION_PARAMETER_BRANCH = "branch";
        private static final String TAG_CONSTRAINT_DEFINITION_PARAMETER_BRANCH_NAME = "name";
        private static final String TAG_CONSTRAINT_DEFINITION_LEVEL = "level";
        private static final String TAG_CONSTRAINT_DEFINITION_LEVEL_GROUP = "group";
        private static final String TAG_CONSTRAINT_DEFINITION_LEVEL_GROUP_NAME = "name";
        private static final String TAG_CONSTRAINT_DEFINITION_LEVEL_GROUP_DESCRIPTION = "description";
        private static final String TAG_CONSTRAINT_DEFINITION_LEVEL_PRIORITY = "priority";
        private static final String TAG_CONSTRAINT_DEFINITION_LEVEL_MESSAGE = "message";
        private static final String TAG_DATA = "data";
        private static final String TAG_DATA_ELEMENT = "dataelement";
        private static final String TAG_DATA_ELEMENT_NAME = "name";
        private static final String TAG_DATA_ELEMENT_TYPE = "type";
        private static final String TAG_DATA_ELEMENT_INITIAL = "initial";
        private static final String TAG_TEAM = "team";
        private static final String TAG_TEAM_ROLE = "teamrole";
        private static final String TAG_TEAM_ROLE_NAME = "name";
        private static final String TAG_TEAM_ROLE_ROLE = "role";

        public AssignmentElementFactory2(XMLBroker2 aBroker) {
            super(aBroker);
        }

        protected Element getAssignmentElement(Element element) {
            return this.getFirstElement(element, "assignment");
        }

        public Element createAssignmentElement(AssignmentModel model) {
            Element element = this.getDocument().createElement("assignment");
            this.assignmentToElement(model, element);
            return element;
        }

        public void assignmentToElement(AssignmentModel model, Element element) {
            this.setAttribute(element, "name", model.getName());
            this.setAttribute(element, "language", model.getLanguage().getName());
            this.attributesToElement(model.getAttributes(), element);
            this.activitiesToElement(model, element);
            this.constraintsToElement(model, element);
        }

        private void activitiesToElement(AssignmentModel model, Element element) {
            Element activities = this.getFirstElement(element, "activitydefinitions");
            this.removeChildren(activities);

            for(int i = 0; i < model.activityDefinitionsCount(); ++i) {
                Element job = this.activityDefinitionToElement(model.activityDefinitionAt(i));
                activities.appendChild(job);
            }

        }

        private void constraintsToElement(AssignmentModel model, Element element) {
            Element constraints = this.getFirstElement(element, "constraintdefinitions");
            this.removeChildren(constraints);

            for(int i = 0; i < model.constraintDefinitionsCount(); ++i) {
                Element constraint = this.constraintDefintionToElement(model.constraintDefinitionAt(i));
                constraints.appendChild(constraint);
            }

        }

        private Element activityDefinitionToElement(ActivityDefinition activity) {
            Element element = this.baseToElement(activity, "activity");
            this.setAttribute(element, "name", activity.getName());
            return element;
        }

        private Element constraintDefintionToElement(ConstraintDefinition constraint) {
            Element element = this.baseToElement(constraint, "constraint");
            Boolean mandatory = Boolean.valueOf(constraint.getMandatory());
            this.setAttribute(element, "mandatory", mandatory.toString());
            Element levelElement = this.constraintLevetToElement(constraint.getLevel());
            if(levelElement != null) {
                element.appendChild(levelElement);
            }

            this.updateObjectAttribute(element, "condition", constraint.getCondition().getText());
            this.updateObjectAttribute(element, "name", constraint.getName());
            TemplateElementFactory2 templateFactory = new TemplateElementFactory2(this);
            Element template = this.getFirstElement(element, "template");
            templateFactory.templateToElement(constraint, template);
            this.parametersToElement(constraint, element);
            return element;
        }

        private Element constraintLevetToElement(ConstraintLevel level) {
            Element element = null;
            if(level != null) {
                element = this.createElement("level");
                Element group = this.constraintGroupToElement(level.getGroup());
                element.appendChild(group);
                this.updateObjectAttribute(element, "priority", Integer.toString(level.getLevel()));
                this.updateObjectAttribute(element, "message", level.getMessage());
            }

            return element;
        }

        private Element constraintGroupToElement(ConstraintGroup group) {
            Element element = this.baseToElement(group, "group");
            this.updateObjectAttribute(element, "name", group.getName());
            this.updateObjectAttribute(element, "description", group.getDescription());
            return element;
        }

        private void parametersToElement(ConstraintDefinition constraint, Element element) {
            Element parameters = this.getFirstElement(element, "constraintparameters");
            this.removeChildren(parameters);
            Iterator i$ = constraint.getParameters().iterator();

            while(i$.hasNext()) {
                Parameter p = (Parameter)i$.next();
                Element parameter = this.parameterToElement(constraint, p);
                parameters.appendChild(parameter);
            }

        }

        private Element parameterToElement(ConstraintDefinition constraintDefinition, Parameter parameter) {
            Element element = this.getDocument().createElement("parameter");
            this.setAttribute(element, "templateparameter", parameter.getIdString());
            this.branchesToElement(constraintDefinition, parameter, element);
            return element;
        }

        private void branchesToElement(ConstraintDefinition constraintDefinition, Parameter parameter, Element element) {
            Element branches = this.getFirstElement(element, "branches");
            this.removeChildren(branches);
            Iterator i$ = constraintDefinition.getBranches(parameter).iterator();

            while(i$.hasNext()) {
                ActivityDefinition real = (ActivityDefinition)i$.next();
                Element branch = this.branchToElement(real);
                branches.appendChild(branch);
            }

        }

        private Element branchToElement(ActivityDefinition branch) {
            Element element = this.createElement("branch");
            this.setAttribute(element, "name", branch.getName());
            return element;
        }

        public AssignmentModel elementToAssignmentModel(Element element) {
            AssignmentModel model = null;
            Element modelElement = this.getFirstElement(element, "assignment");
            if(modelElement != null) {
                Language lang = Control.singleton().getConstraintTemplate().getLanguage(modelElement.getAttribute("language"));
                if(lang != null) {
                    model = new AssignmentModel(lang);
                    String name = modelElement.getAttribute("name");
                    model.setName(name);
                    this.elementToAttributes(modelElement, model.getAttributes());
                    Element rolesTag = this.getFirstElement(modelElement, "team");
                    NodeList roleElements = rolesTag.getElementsByTagName("teamrole");

                    for(int dataTag = 0; dataTag < roleElements.getLength(); ++dataTag) {
                        roleElements.item(dataTag);
                    }

                    Element var16 = this.getFirstElement(modelElement, "data");
                    NodeList dataElements = var16.getElementsByTagName("dataelement");

                    for(int jobsTag = 0; jobsTag < dataElements.getLength(); ++jobsTag) {
                        dataElements.item(jobsTag);
                    }

                    Element var17 = this.getFirstElement(modelElement, "activitydefinitions");
                    NodeList jobs = var17.getElementsByTagName("activity");

                    for(int constraintsTag = 0; constraintsTag < jobs.getLength(); ++constraintsTag) {
                        Element constraints = (Element)jobs.item(constraintsTag);
                        this.elementToActivityDefinition(model, constraints);
                    }

                    Element var18 = this.getFirstElement(modelElement, "constraintdefinitions");
                    NodeList var19 = var18.getElementsByTagName("constraint");

                    for(int i = 0; i < var19.getLength(); ++i) {
                        Element constraint = (Element)var19.item(i);
                        this.elementToConstraintDeintion(model, constraint);
                    }
                }
            }

            return model;
        }

        private ConstraintDefinition elementToConstraintDeintion(AssignmentModel model, Element element) {
            Base base = this.elementToBase(element);
            String conditionText = this.getSimpleElementText(element, "condition");
            String mandatoryText = element.getAttribute("mandatory");
            Boolean mandatory = new Boolean(mandatoryText);
            String name = this.getSimpleElementText(element, "name");
            ConstraintLevel level = this.elementToConstraintLevel(element);
            TemplateElementFactory2 templateFactory = new TemplateElementFactory2(this);
            Element templateElement = this.getFirstElement(element, "template");
            ConstraintTemplate template = templateFactory.elementToTemplate(model.getLanguage(), templateElement);
            ConstraintDefinition constraint = new ConstraintDefinition(base.getId(), model, template);
            model.addConstraintDefiniton(constraint);
            Element parametersTag = this.getFirstElement(element, "constraintparameters");
            NodeList parameters = parametersTag.getElementsByTagName("parameter");
            ArrayList params = new ArrayList();

            int i;
            for(i = 0; i < parameters.getLength(); ++i) {
                Element parameter = (Element)parameters.item(i);
                Parameter i$ = this.elementToParameter(model, constraint, parameter);
                params.add(i$);
            }

            for(i = 0; i < params.size(); ++i) {
                Parameter var20 = (Parameter)params.get(i);
                Iterator var21 = constraint.getBranches(var20).iterator();

                while(var21.hasNext()) {
                    ActivityDefinition real = (ActivityDefinition)var21.next();
                    constraint.addBranch(var20, real);
                }
            }

            if(constraint != null) {
                constraint.getCondition().setText(conditionText);
                constraint.setName(name);
                constraint.setMandatory(mandatory.booleanValue());
                if(!mandatory.booleanValue()) {
                    constraint.setLevel(level);
                }
            }

            return constraint;
        }

        private ConstraintLevel elementToConstraintLevel(Element element) {
            ConstraintLevel level = null;
            if(element != null) {
                Element levelElement = this.getFirstElement(element, "level");
                if(levelElement != null) {
                    ConstraintGroup group = this.elementToConstraintGroup(levelElement);
                    if(group != null) {
                        level = new ConstraintLevel(group);
                        String priority = this.getSimpleElementText(levelElement, "priority");
                        String message = this.getSimpleElementText(levelElement, "message");
                        int pr = ConstraintWarningLevel.possible()[0].intValue();

                        try {
                            pr = Integer.parseInt(priority);
                        } catch (Exception var9) {
                            ;
                        }

                        level.setLevel(pr);
                        level.setMessage(message);
                    }
                }
            }

            return level;
        }

        private ConstraintGroup elementToConstraintGroup(Element element) {
            ConstraintGroup group = null;
            if(element != null) {
                Element groupElement = this.getFirstElement(element, "group");
                if(groupElement != null) {
                    Base base = this.elementToBase(groupElement);
                    group = new ConstraintGroup(base.getId());
                    String name = this.getSimpleElementText(groupElement, "name");
                    String description = this.getSimpleElementText(groupElement, "description");
                    group.setName(name);
                    group.setDescription(description);
                }
            }

            return group;
        }

        private Parameter elementToParameter(AssignmentModel model, ConstraintDefinition constraintDefinition, Element element) {
            String templateParam = element.getAttribute("templateparameter");
            Parameter parameter = constraintDefinition.getParameterWithId(Integer.decode(templateParam).intValue());
            Element branchesTag = this.getFirstElement(element, "branches");
            NodeList branches = branchesTag.getElementsByTagName("branch");

            for(int i = 0; i < branches.getLength(); ++i) {
                Element branch = (Element)branches.item(i);
                String name = branch.getAttribute("name");
                ActivityDefinition activityDefinition = model.activityDefinitionWithName(name);
                constraintDefinition.addBranch(parameter, activityDefinition);
            }

            return parameter;
        }

        private void elementToActivityDefinition(AssignmentModel model, Element element) {
            Base base = this.elementToBase(element);
            ActivityDefinition activityDefinition = model.addActivityDefinition(base.getId());
            String name = element.getAttribute("name");
            activityDefinition.setName(name);
            this.getFirstElement(element, "authorization");
            this.getFirstElement(element, "datamodel");
        }
    }
