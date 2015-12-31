package com.tangdi;

public class Users
{
  private String phone;
  private String cusNam;
  private String idNo;
  private String bthDay;
  private String GRP_NAM;

  public String getGRP_NAM()
  {
    return this.GRP_NAM; }

  public void setGRP_NAM(String grp_nam) {
    this.GRP_NAM = grp_nam;
  }

  public String getPhone() {
    return this.phone; }

  public void setPhone(String phone) {
    this.phone = phone; }

  public String getIdNo() {
    return this.idNo; }

  public void setIdNo(String idNo) {
    this.idNo = idNo; }

  public String getBthDay() {
    return this.bthDay; }

  public void setBthDay(String bthDay) {
    this.bthDay = bthDay; }

  public String getCusNam() {
    return this.cusNam; }

  public void setCusNam(String cusNam) {
    this.cusNam = cusNam;
  }
}