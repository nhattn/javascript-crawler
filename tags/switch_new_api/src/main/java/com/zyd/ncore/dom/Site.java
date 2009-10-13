package com.zyd.ncore.dom;

import com.zyd.ncore.Utils;

public class Site {
    public String id;
    public String name;
    public String domainName;
    public String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "[site: " + domainName + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj instanceof Site == false)
            return false;
        return Utils.strictEqual(domainName, ((Site) obj).getDomainName());
    }

    @Override
    public int hashCode() {
        return domainName.hashCode();
    }
}
