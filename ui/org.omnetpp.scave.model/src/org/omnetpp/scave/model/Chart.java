
package org.omnetpp.scave.model;

import java.util.ArrayList;
import java.util.List;

import java.util.Collections;

public class Chart extends AnalysisItem {

    public static class DialogPage {

        public DialogPage(DialogPage other) {
            this.id = other.id;
            this.label = other.label;
            this.xswtForm = other.xswtForm;
        }

        public DialogPage(String id, String label, String xswtForm) {
            this.id = id;
            this.label = label;
            this.xswtForm = xswtForm;
        }

        public String id;
        public String label;
        public String xswtForm;
    }

    public static enum ChartType {
        BAR,
        HISTOGRAM,
        LINE,
        MATPLOTLIB
    }

    protected String script;
    protected List<Property> properties = new ArrayList<Property>();
    protected boolean temporary;
    protected String templateID;
    protected List<DialogPage> dialogPages = new ArrayList<>();
    protected ChartType type;
    protected String iconPath = "";

    public Chart() {
    }

    public Chart(ChartType type) {
        this.type = type;
    }

    public Chart(ChartType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
        notifyListeners();
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public Property lookupProperty(String name) {
        for (Property p : properties) {
            if (name.equals(p.getName()))
                return p;
        }
        return null;
    }

    public void setProperties(List<Property> properties) {
        for (Property p : this.properties)
            p.parent = null;
        this.properties = properties;
        for (Property p : this.properties)
            p.parent = this;
        notifyListeners();
    }

    public void addProperty(Property property) {
        property.parent = this;
        properties.add(property);
        notifyListeners();
    }

    public void removeProperty(Property property) {
        property.parent = null;
        properties.remove(property);
        notifyListeners();
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
        notifyListeners();
    }

    public String getTemplateID() {
        return templateID;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
        notifyListeners();
    }

    public List<DialogPage> getDialogPages() {
        return Collections.unmodifiableList(dialogPages);
    }

    public void setDialogPages(List<DialogPage> dialogPages) {
        this.dialogPages = dialogPages;
        notifyListeners();
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
        notifyListeners();
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
        notifyListeners();
    }


    @Override
    protected Chart clone() throws CloneNotSupportedException {
        Chart clone = (Chart) super.clone();

        clone.properties = new ArrayList<Property>(properties.size());

        for (int i = 0; i < properties.size(); ++i)
            clone.properties.add(properties.get(i).clone());

        clone.dialogPages = new ArrayList<Chart.DialogPage>(dialogPages.size());

        for (int i = 0; i < dialogPages.size(); ++i)
            clone.dialogPages.add(new Chart.DialogPage(dialogPages.get(i)));

        return clone;
    }
}
