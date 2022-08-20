package com.github.wnebyte.editor.project;

import java.util.List;
import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public final class Assets {

    @XmlElement(name = "tab")
    private List<Tab> tabs;

    public Assets() {}

    public Assets(List<Tab> tabs) {
        this.tabs = tabs;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }

    public List<Tab> getTabs() {
        return tabs;
    }

    public void addTab(Tab tab) {
        tabs.add(tab);
    }
}
