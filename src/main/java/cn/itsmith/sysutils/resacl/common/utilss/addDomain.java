package cn.itsmith.sysutils.resacl.common.utilss;

import lombok.Data;

//域，
@Data
public class addDomain {
    String domDes;
    String domName;

    public String getDomName() {
        return domName;
    }

    public void setDomName(String domName) {
        this.domName = domName;
    }

    public String getDomDes() {
        return domDes;
    }

    public void setDomDes(String domDes) {
        this.domDes = domDes;
    }
}
