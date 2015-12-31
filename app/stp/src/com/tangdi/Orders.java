package com.tangdi;

public class Orders
{
  private String prdOrdNo;
  private String ordAmt;
  private String orderDate;
  private String orderTime;
  private String ordStatus;
  private String transType;
  private String prdName;

  public String getPrdOrdNo()
  {
    return this.prdOrdNo; }

  public void setPrdOrdNo(String prdOrdNo) {
    this.prdOrdNo = prdOrdNo; }

  public String getOrdAmt() {
    return this.ordAmt; }

  public void setOrdAmt(String ordAmt) {
    this.ordAmt = ordAmt; }

  public String getOrderDate() {
    return this.orderDate; }

  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate; }

  public String getOrderTime() {
    return this.orderTime; }

  public void setOrderTime(String orderTime) {
    this.orderTime = orderTime; }

  public String getTransType() {
    return this.transType; }

  public String getOrdStatus() {
    return this.ordStatus; }

  public void setOrdStatus(String ordStatus) {
    this.ordStatus = ordStatus; }

  public void setTransType(String transType) {
    this.transType = transType; }

  public String getPrdName() {
    return this.prdName; }

  public void setPrdName(String prdName) {
    this.prdName = prdName;
  }
}