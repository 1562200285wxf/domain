package cn.itsmith.sysutils.resacl.common.utilss;

public class ChangeResourceTypeDes {
    private Integer restypeid;

    private Integer domid;

    private String restypedes;

    public Integer getRestypeid() {
        return restypeid;
    }

    public void setRestypeid(Integer restypeid) {
        this.restypeid = restypeid;
    }

    public Integer getDomid() {
        return domid;
    }

    public void setDomid(Integer domid) {
        this.domid = domid;
    }

    public String getRestypedes() {
        return restypedes;
    }

    public void setRestypedes(String restypedes) {
        this.restypedes = restypedes == null ? null : restypedes.trim();
    }

}
