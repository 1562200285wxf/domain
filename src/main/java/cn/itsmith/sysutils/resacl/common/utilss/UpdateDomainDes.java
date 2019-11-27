package cn.itsmith.sysutils.resacl.common.utilss;

import lombok.Data;

@Data
public class UpdateDomainDes {
    public Integer domid;
    public String domdes;

    public Integer getDomid() {
        return domid;
    }

    public void setDomid(Integer domid) {
        this.domid = domid;
    }

    public String getDomdes() {
        return domdes;
    }

    public void setDomdes(String domdes) {
        this.domdes = domdes== null ? null : domdes.trim();
    }
}