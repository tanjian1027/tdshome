package com.bean;

public class MerSeltBean
{
  private String DATETIME;
  private String CUST_ID;
  private String CUST_NAME;
  private String STL_AMT;
  private String STL_FEE;
  private String NUMB;

  public String getDATETIME()
  {
    return this.DATETIME; }

  public void setDATETIME(String dATETIME) {
    this.DATETIME = dATETIME; }

  public String getCUST_ID() {
    return this.CUST_ID; }

  public void setCUST_ID(String CUST_ID) {
    this.CUST_ID = CUST_ID; }

  public String getCUST_NAME() {
    return this.CUST_NAME; }

  public void setCUST_NAME(String CUST_NAME) {
    this.CUST_NAME = CUST_NAME; }

  public String getSTL_FEE() {
    return this.STL_FEE; }

  public void setSTL_FEE(String STL_FEE) {
    this.STL_FEE = STL_FEE; }

  public String getSTL_AMT() {
    return this.STL_AMT; }

  public void setSTL_AMT(String STL_AMT) {
    this.STL_AMT = STL_AMT; }

  public String getNUMB() {
    return this.NUMB; }

  public void setNUMB(String NUMB) {
    this.NUMB = NUMB;
  }
}