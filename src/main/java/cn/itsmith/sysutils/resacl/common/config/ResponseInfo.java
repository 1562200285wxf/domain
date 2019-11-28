package cn.itsmith.sysutils.resacl.common.config;

public enum ResponseInfo {
    SUCCESS(200,"SUCCESS"),

    ADDDATA_ERROR(2001,"数据添加失败"),

    //权限不足
    AUTH_ERROR(3001, "操作员权限不足"),
    //域认证异常
    NOTOKEN_ERROR(1000, "域令牌无效"),
    DOMAINAUTH_ERROR(1001,"域或域令牌错误"),
    DOMAINMATCH_ERROR(1002, "操作员或操作数据与域不匹配"),

    //数据库操作异常
    GETDOMAIN_ERROR(2001, "根据域标识未获取到域信息"),
    GETOWNER_ERROR(2002, "该域下不存在此资源属主"),
    GETRESTYPE_ERROR(2003, "该域下不存在此资源类型"),
    GETOWNERRES_ERROR(2004, "该属主下没有此资源类型"),
    GETRESINTANCE_ERROR(2005, "该资源类型下没有此资源实例"),
    GETRESOPERATION_ERROR(2006, "该资源可用权限不存在"),
    GETUSER_ERROR(2007, "属主下不存在该用户"),
    OWNERPID_ERROR(2008, "添加属主的父属主不存在"),
    RESEXIST_ERROR(2009, "添加的实例已经存在"),
    OPEXIST_ERROR(2010, "添加的资源可用实例已经存在"),

    //资源授权

    OWNERUSINGP_ERROR(4000, "此属主是其他属主的父属主，不能删除"),
    OWNERUSINGU_ERROR(4001, "此属主还拥有属主成员，不能删除"),
    OWNERUSINGT_ERROR(4002, "此属主还拥有资源种类，不能删除"),
    OPUSING_ERROR(4003, "此资源可用权限还在使用中"),

    OPEXTEND1_ERROR(5001, "该资源可用权限已经是可继承"),
    OPEXTEND2_ERROR(5002, "该资源可用权限已经是不可继承"),
    OPCOMMON1_ERROR(5003, "该资源可用权限已经是通用的"),
    OPCOMMON2_ERROR(5004, "该资源可用权限已经是不通用的"),
    //wang  我
    //认证异常
    DOMAIN_IS(2001,"域存在"),
    DOMAIN_NOT(2002,"域不存在"),
    SUCCESS_IS(2003,"操作成功"),
    FALSE_IS(2004,"操作失败"),

    ResType_IS(2005,"域存在"),
    ResType_NOT(2005,"域存在"),






    //liu
    AUTH_FAILED(1001,"token和域不匹配，认证失败"),
    OWNER_FAILED(1002,"你选的域下此属主不存在"),

    //属主成员
    //添加
    DUMPILCATE_ERROR(1003,"该成员已经添加到domOwnerUser表过了，不能重复添加"),
    NONUSER_ERROR(1004,"成员不存在于基本表，同样不能添加"),
    //删除和管理员异常（1005已经弃用X）
    OWNER_FAILED2(1005,"此属主不存在于Userdom表中，不能执行删除操作"),
    //删除
    NONUSER_ERROR2(1006,"成员不存在于指定的domOwner【该域下没有你输的成员（ownerId+userId错误）】，删除是非法操作.必须先添加成员到你选择的dom才能删除"),
    //管理员
    NONUSER_ERROR3(1007,"成员不存在于指定的domOwner【该域下没有你输的成员（ownerId+userId错误）】，设置管理员是非法操作.必须先添加成员才能设置管理员"),
    NONUSER_ERROR4(1008,"成员不存在于指定的domOwner【该域下没有你输的成员（ownerId+userId错误）】，取消管理员是非法操作.必须先添加成员才能取消管理员"),



    //属拥有资源品种
    //添加
    DUMPILCATERES_ERROR(2001,"资源已经添加加到你一开始选择的dom过了，不能重复添加"),
    NONRES_ERROR(2002,"基本表中不存在和你一开始选择的dom匹配owner也匹配的资源种类【这个基本表指的是dom_res_type】，同样不能添加"),
    //删除
    OWNER_FAILED3(2003,"对应于此域，该属主不存在于ownerRes表中，不能执行删除操作"),
    NONRES_ERROR2(2004,"资源不存在于ownerRes（要么该域下没有你输的资源（resTypeId错误）、要么你的操作跨域了（domId错误）），删除是非法操作.必须先添加资源到ownerRes才能删除"),
    BEINGUSED_ERROR(2006,"资源实例中正在使用此资源类型，不能删除"),
    //查询
    NONRES_ERROR3(2007,"ownerRes逻辑表查询不到任何属主拥有的资源"),

    //资源授权
    NONOPERATION_ERROR(3001,"该用户没有资源授权记录List==null"),
    ALREADYOPERATION_ERROR(3002,"已存在此条授权记录，不能重复添加"),
    NONOPERATION_ERROR2(3003,"不存在此条授权记录，不能删除"),



    //系统错误异常
    UNKNOWN_ERROR(8000, "未知错误"),

    PARAM_ERROR(9000, "参数格式错误"),

    SERVER_ERROR(9999, "服务器异常");

    private int errorCode;
    private String errorMessage;

    ResponseInfo(int code, String msg) {
        this.errorCode = code;
        this.errorMessage = msg;
    }
    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMessage;
    }

    public static String getResponseErrorMsg(String code) {

        for(ResponseInfo responseInfo: ResponseInfo.values()) {

            if(code.equals(responseInfo.getErrorCode())) {

                return responseInfo.getErrorMsg();
            }
        }
        return SERVER_ERROR.getErrorMsg();
    }

    public static int getResponseErrorCode(String msg) {
        if(msg == null) {

            return UNKNOWN_ERROR.getErrorCode();
        }
        for(ResponseInfo responseInfo: ResponseInfo.values()) {

            if(msg.equals(responseInfo.getErrorMsg())) {
                return responseInfo.getErrorCode();
            }
        }
        return SERVER_ERROR.getErrorCode();
    }
}

