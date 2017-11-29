package au.ltl.extendedReader;

    import java.util.ArrayList;
    import java.util.Iterator;
    import java.util.Set;
    import java.util.Map;
    import org.processmining.plugins.declare.visualizing.ConstraintTemplate;
    import org.processmining.plugins.declare.visualizing.IItem;
    import org.processmining.plugins.declare.visualizing.Language;
    import org.processmining.plugins.declare.visualizing.LanguageGroup;
    import org.processmining.plugins.declare.visualizing.LineStyle;
    import org.processmining.plugins.declare.visualizing.Parameter;
    import org.processmining.plugins.declare.visualizing.State;
    import org.w3c.dom.Element;
    import org.w3c.dom.Node;
    import org.w3c.dom.NodeList;

    public class TemplateElementFactory2 extends XMLElementFactory2 {
        private static final String TAG_LANGUAGE = "language";
        private static final String TAG_LANGUAGE_NAME = "name";
        private static final String TAG_GROUP = "group";
        private static final String TAG_GROUP_NAME = "name";
        private static final String TAG_TEMPL = "template";
        private static final String TAG_DESCRIPTION = "description";
        private static final String TAG_DISPLAY = "display";
        private static final String TAG_FORMULA_NAME = "name";
        private static final String TAG_FORMULA_TEXT = "text";
        private static final String TAG_FORMULA_PARAMETERS = "parameters";
        private static final String TAG_FORMULA_PARAMETER = "parameter";
        private static final String TAG_FORMULA_PARAMETER_NAME = "name";
        private static final String TAG_FORMULA_PARAMETER_BRANCHABLE = "branchable";
        private static final String TAG_STATE_MESSAGES = "statemessages";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_STATE = "state";
        private static final String TAG_GRAPHICAL = "graphical";
        private static final String TAG_LINE_NUMBER = "number";
        private static final String TAG_LINE_STYLE = "style";
        private static final String TAG_LINE_STYLE_ITEM = "item";
        private static final String TAG_LINE_STYLE_VALUE = "value";
        private static final String TAG_BEGIN = "begin";
        private static final String TAG_BEGIN_STYLE = "style";
        private static final String TAG_BEGIN_FILL = "fill";
        private static final String TAG_MIDDLE = "middle";
        private static final String TAG_MIDDLE_STYLE = "style";
        private static final String TAG_MIDDLE_FILL = "fill";
        private static final String TAG_END = "end";
        private static final String TAG_END_STYLE = "style";
        private static final String TAG_END_FILL = "fill";
        private static final String TAG_EVENT = "event";
        private static final String TAG_EVENT_TYPE = "type";
        private static final String TAG_EVENT_USER = "user";
        private static final String TAG_EVENT_JOB = "job";

        public TemplateElementFactory2(XMLBroker2 broker) {
            super(broker);
        }

        public TemplateElementFactory2(XMLElementFactory2 factory) {
            super(factory);
        }

        public Element parameterToElement(Parameter parameter) {
            Element element = this.baseToElement(parameter, "parameter");
            element.setAttribute("name", parameter.getName());
            element.setAttribute("branchable", Boolean.toString(parameter.isBranchable()));
            this.lineStyleToElement(parameter.getStyle(), this.getFirstElement(element, "graphical"));
            return element;
        }

        public Language elementToLanguage(Element element) {
            int id = this.elementToBase(element).getId();
            Language lang = new Language(id, element.getAttribute("name"));
            this.fillLanguage(element, lang, lang);
            return lang;
        }

        private void fillLanguage(Element element, LanguageGroup parent, Language lang) {
            NodeList children = element.getChildNodes();
            if(element.getNodeName().equals("template")) {
                parent.add(this.elementToTemplate(lang, element));
            } else {
                Object group = null;
                if(element.getNodeName().equals("group")) {
                    group = this.elementToGroup(element);
                    parent.add((IItem)group);
                } else {
                    group = lang;
                }

                for(int i = 0; i < children.getLength(); ++i) {
                    Node node = children.item(i);
                    if(node instanceof Element) {
                        this.fillLanguage((Element)node, (LanguageGroup)group, lang);
                    }
                }
            }

        }

        public ConstraintTemplate elementToTemplate(Language lang, Element element) {
            String descrition = this.getSimpleElementText(element, "description");
            String display = this.getSimpleElementText(element, "display");
            String name = this.getSimpleElementText(element, "name");
            String text = this.getSimpleElementText(element, "text");
            Element parametersTag = this.getFirstElement(element, "parameters");
            NodeList parameters = parametersTag.getElementsByTagName("parameter");
            ArrayList parametersList = new ArrayList();

            int id;
            for(id = 0; id < parameters.getLength(); ++id) {
                Element template = (Element)parameters.item(id);
                String stateMessagesTag = template.getAttribute("name");
                String stateMessages = template.getAttribute("branchable");
                boolean i = Boolean.parseBoolean(stateMessages);
                Parameter stateMessage = new Parameter(this.elementToBase(template).getId(), stateMessagesTag);
                stateMessage.setBranchable(i);
                Element state = this.getFirstElement(template, "graphical");
                this.elementToLineStyle(state, stateMessage.getStyle());
                parametersList.add(stateMessage);
            }

            id = this.elementToBase(element).getId();
            ConstraintTemplate var18 = new ConstraintTemplate(id, lang);

            for(int var19 = 0; var19 < parametersList.size(); ++var19) {
                var18.addParameter((Parameter)parametersList.get(var19));
            }

            if(var18 != null) {
                var18.setName(name);
                var18.setText(text);
            }

            if(var18 != null) {
                var18.setDescription(descrition);
                var18.setDisplay(display);
            }

            Element var20 = this.getFirstElement(element, "statemessages");
            NodeList var21 = var20.getElementsByTagName("message");

            for(int var22 = 0; var22 < var21.getLength(); ++var22) {
                Element var23 = (Element)var21.item(var22);
                String var24 = var23.getAttribute("state");
                String message = var23.getFirstChild().getNodeValue();
                var18.setStateMessage(State.valueOf(var24), message);
            }

            return var18;
        }

        public NodeList getListLanguages(Element element) {
            return element.getElementsByTagName("language");
        }

        public NodeList getListTemplates(Element element) {
            NodeList list = element.getElementsByTagName("template");
            return list;
        }

        public void elementToLineStyle(Element element, LineStyle style) {
            this.elementToLine(element, style);
        }

        public void elementToLine(Element element, LineStyle line) {
            Element styleTag = this.getFirstElement(element, "style");
            Element beginTag = this.getFirstElement(element, "begin");
            Element middleTag = this.getFirstElement(element, "middle");
            Element endTag = this.getFirstElement(element, "end");
            String number = styleTag.getAttribute("number");
            int nr = 1;

            try {
                nr = Integer.parseInt(number);
            } catch (NumberFormatException var24) {
                ;
            }

            NodeList items = styleTag.getElementsByTagName("item");
            float[] f = null;
            if(items.getLength() > 0) {
                f = new float[items.getLength()];

                for(int beginStyle = 0; beginStyle < items.getLength(); ++beginStyle) {
                    Element beginFill = (Element)items.item(beginStyle);
                    String bs = beginFill.getAttribute("value");
                    if(bs != null) {
                        f[beginStyle] = Float.parseFloat(bs);
                    }
                }
            }

            String var25 = beginTag.getAttribute("style");
            String var26 = beginTag.getAttribute("fill");
            int var27 = Integer.parseInt(var25);
            boolean bf = Boolean.parseBoolean(var26);
            String middleStyle = middleTag.getAttribute("style");
            String middleFill = middleTag.getAttribute("fill");
            int ms = 0;

            try {
                ms = Integer.parseInt(middleStyle);
            } catch (NumberFormatException var23) {
                ;
            }

            boolean mf = Boolean.parseBoolean(middleFill);
            String endStyle = endTag.getAttribute("style");
            String endFill = endTag.getAttribute("fill");
            int es = Integer.parseInt(endStyle);
            boolean ef = Boolean.parseBoolean(endFill);
            line.setLine(f);
            line.setNumber(nr);
            line.setBegin(var27);
            line.setBeginFill(bf);
            line.setMiddle(ms);
            line.setMiddleFill(mf);
            line.setEnd(es);
            line.setEndFill(ef);
        }

        public void templateToElement(ConstraintTemplate template, Element element) {
            this.updateObjectAttribute(element, "description", template.getDescription());
            this.updateObjectAttribute(element, "display", template.getDisplay());
            this.updateObjectAttribute(element, "name", template.getName());
            this.updateObjectAttribute(element, "text", template.getText());
            Element parameters = this.getFirstElement(element, "parameters");
            this.removeChildren(parameters);
            Iterator stateMessages = template.getParameters().iterator();

            while(stateMessages.hasNext()) {
                Parameter msgs = (Parameter)stateMessages.next();
                Element i$ = this.parameterToElement(msgs);
                parameters.appendChild(i$);
            }

            Element stateMessages1 = this.getFirstElement(element, "statemessages");
            this.removeChildren(stateMessages1);
            Set msgs1 = template.getStateMessages();
            Iterator i$1 = msgs1.iterator();

            while(i$1.hasNext()) {
                Map.Entry entry = (Map.Entry)i$1.next();
                Element message = this.createObjectAttribute("message", (String)entry.getValue());
                message.setAttribute("state", ((State)entry.getKey()).name());
                stateMessages1.appendChild(message);
            }

        }

        public void updateTemplateElement(ConstraintTemplate template, Element element) {
            this.templateToElement(template, element);
        }

        public void lineStyleToElement(LineStyle line, Element element) {
            Element styleTag = this.getFirstElement(element, "style");
            Element beginTag = this.getFirstElement(element, "begin");
            Element middleTag = this.getFirstElement(element, "middle");
            Element endTag = this.getFirstElement(element, "end");
            new Float(0.0F);
            float[] array = line.getLine();
            this.removeChildren(styleTag);
            if(array != null) {
                for(int i = 0; i < array.length; ++i) {
                    Float item = Float.valueOf(array[i]);
                    Element styleItem = this.createElement("item");
                    styleItem.setAttribute("value", item.toString());
                    styleTag.appendChild(styleItem);
                }
            }

            styleTag.setAttribute("number", String.valueOf(line.getNumber()));
            beginTag.setAttribute("style", String.valueOf(line.getBegin()));
            beginTag.setAttribute("fill", String.valueOf(line.isBeginFill()));
            middleTag.setAttribute("style", String.valueOf(line.getMiddle()));
            middleTag.setAttribute("fill", String.valueOf(line.isMiddleFill()));
            endTag.setAttribute("style", String.valueOf(line.getEnd()));
            endTag.setAttribute("fill", String.valueOf(line.isEndFill()));
        }

        public Element getTemplateElement(ConstraintTemplate template, Element element) {
            NodeList templates = element.getElementsByTagName("template");
            boolean found = false;
            Element current = null;

            ConstraintTemplate currentTemplate;
            for(int i = 0; !found && i < templates.getLength(); found = currentTemplate.equals(template)) {
                current = (Element)templates.item(i++);
                currentTemplate = this.elementToTemplate(template.getLanguage(), current);
            }

            return found?current:null;
        }

        public Element createTemplateElement(ConstraintTemplate template) {
            Element element = this.baseToElement(template, "template");
            this.updateTemplateElement(template, element);
            return element;
        }

        public Element createGroupElement(LanguageGroup group) {
            Element element = this.baseToElement(group, "group");
            this.updateGroupElement(group, element);
            return element;
        }

        public void updateGroupElement(LanguageGroup group, Element element) {
            this.groupToElement(group, element);
        }

        public void groupToElement(LanguageGroup group, Element element) {
            element.setAttribute("name", group.getName());
        }

        public Element createLanguageElement(Language language) {
            Element element = this.baseToElement(language, "language");
            this.setAttribute(element, "name", language.getName());
            Iterator it = language.getChildren().iterator();

            while(it.hasNext()) {
                IItem item = (IItem)it.next();
                if(item instanceof ConstraintTemplate) {
                    ConstraintTemplate tem = (ConstraintTemplate)item;
                    Element newTemplate = this.createTemplateElement(tem);
                    element.appendChild(newTemplate);
                }
            }

            return element;
        }

        public Element getLanguageElement(Element element, Language lang) {
            Element langElement = null;
            NodeList languages = element.getElementsByTagName("language");
            boolean found = false;

            for(int i = 0; i < languages.getLength() && !found; found = langElement.getAttribute("name").equals(lang.getName())) {
                langElement = (Element)languages.item(i++);
            }

            return found?langElement:null;
        }

        public LanguageGroup elementToGroup(Element element) {
            int id = this.elementToBase(element).getId();
            LanguageGroup group = new LanguageGroup(id);
            group.setName(element.getAttribute("name"));
            return group;
        }

        public Element getGroupElement(Element element, LanguageGroup group) {
            Element groupElement = null;
            Iterator groups = this.getAllSubElements(element, "group").iterator();
            LanguageGroup temp = null;

            boolean found;
            for(found = false; groups.hasNext() && !found; found = temp.equals(group)) {
                groupElement = (Element)groups.next();
                temp = this.elementToGroup(groupElement);
            }

            return found?groupElement:null;
        }
    }