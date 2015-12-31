package com.tangdi;

public class Ordersmer
{
  private String PrdOrdNo;
  private String OrdAmt;
  private String OrderDate;
  private String OrderTime;
  private String OrdStatus;
  private String TransType;
  private String PrdName;

  public String getPrdOrdNo()
  {
    return this.PrdOrdNo; }

  public void setPrdOrdNo(String prdOrdNo) {
    this.PrdOrdNo = prdOrdNo; }

  public String getOrdAmt() {
    return this.OrdAmt; }

  public void setOrdAmt(String ordAmt) {
    this.OrdAmt = ordAmt; }

  public String getOrderDate() {
    return this.OrderDate; }

  public void setOrderDate(String orderDate) {
    this.OrderDate = orderDate; }

  public String getOrderTime() {
    return this.OrderTime; }

  public void setOrderTime(String orderTime) {
    this.OrderTime = orderTime; }

  public String getOrdStatus() {
    return this.OrdStatus; }

  public void setOrdStatus(String ordStatus) {
    this.OrdStatus = ordStatus; }

  public String getTransType() {
    return this.TransType; }

  public void setTransType(String transType) {
    this.TransType = transType; }

  public String getPrdName() {
    return this.PrdName; }

  public void setPrdName(String prdName) {
    this.PrdName = prdName;
  }
}