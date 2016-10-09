package sk.homisolutions.shotbox.tools.models;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;

/**
 * Created by homi on 10/8/16.
 */
public class ShotBoxConnection {
    private String protocol;
    private String address;
    private ShotBoxExternalModule module;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ShotBoxExternalModule getModule() {
        return module;
    }

    public void setModule(ShotBoxExternalModule module) {
        this.module = module;
    }
}
