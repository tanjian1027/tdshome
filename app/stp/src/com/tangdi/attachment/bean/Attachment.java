package com.tangdi.attachment.bean;

/*
 * @author Jason Meng
 * @desc 附件实体表 
 * */
public class Attachment {
	private String id;
    private String moduleName;
    private String tableName;
    private String pkId;
    private String lx;
    private String orderNum;
    private String fjName;
    private String fjPath;
    private String fjo;
    private String fjt;
    private String sfsx;
    private String abateDate;
    public String getAbateDate() {
		return abateDate;
	}
	public void setAbateDate(String abateDate) {
		this.abateDate = abateDate;
	}
	public String getSfsx() {
		return sfsx;
	}
	public void setSfsx(String sfsx) {
		this.sfsx = sfsx;
	}
	public Attachment(){}
    public Attachment(String id,String moduleName,String tableName,String pkId,
    		String lx,String orderNum,String fjName,String fjPath,String fjo,String fjt){
    	this.id 		= id;
    	this.moduleName = moduleName;
    	this.tableName 	= tableName;
    	this.pkId 		= pkId;
    	this.lx 		= lx;
    	this.orderNum 	= orderNum;
    	this.fjName 	= fjName;
    	this.fjPath 	= fjPath;
    	this.fjo 		= fjo;
    	this.fjt 		= fjt;
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPkId() {
		return pkId;
	}
	public void setPkId(String pkId) {
		this.pkId = pkId;
	}
	public String getLx() {
		return lx;
	}
	public void setLx(String lx) {
		this.lx = lx;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getFjName() {
		return fjName;
	}
	public void setFjName(String fjName) {
		this.fjName = fjName;
	}
	public String getFjPath() {
		return fjPath;
	}
	public void setFjPath(String fjPath) {
		this.fjPath = fjPath;
	}
	public String getFjo() {
		return fjo;
	}
	public void setFjo(String fjo) {
		this.fjo = fjo;
	}
	public String getFjt() {
		return fjt;
	}
	public void setFjt(String fjt) {
		this.fjt = fjt;
	}
}