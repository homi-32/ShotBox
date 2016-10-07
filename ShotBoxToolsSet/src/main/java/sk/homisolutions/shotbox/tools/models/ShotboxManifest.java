package sk.homisolutions.shotbox.tools.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by homi on 10/6/16.
 */
public class ShotboxManifest {
    private String name;
    private List<String> implementation_packages;
    private List<String> settings_packages;
    private String icon;

    private String author;
    private String version;
    private String description;
    private List<String> contacts;
    private String update_url;
    private String version_name;

    public ShotboxManifest(){
        name = "";
        implementation_packages = new ArrayList<>();
        settings_packages = new ArrayList<>();
        icon = "";
        author = "";
        version = "";
        description = "";
        contacts = new ArrayList<>();
        update_url = "";
        version_name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImplementation_packages() {
        return implementation_packages;
    }

    public void setImplementation_packages(List<String> implementation_packages) {
        this.implementation_packages = implementation_packages;
    }

    public List<String> getSettings_packages() {
        return settings_packages;
    }

    public void setSettings_packages(List<String> settings_packages) {
        this.settings_packages = settings_packages;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }
}
