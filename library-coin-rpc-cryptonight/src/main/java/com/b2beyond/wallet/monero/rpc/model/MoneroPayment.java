package com.b2beyond.wallet.monero.rpc.model;

import java.math.BigInteger;

/**
 * Represents a payment on the Monero network to an address.
 * 
 * A transaction may have one or more payments.
 * 
 * Each payment may be fulfilled by multiple transaction outputs.
 * 
 * @author woodser
 */
public class MoneroPayment {

  private MoneroTransaction transaction;
  private String address;
  private BigInteger amount;
  
  public MoneroPayment() {
    super();
  }
  
  public MoneroPayment(String address, BigInteger amount) {
    this(null, address, amount);
  }
  
  public MoneroPayment(MoneroTransaction transaction, String address, BigInteger amount) {
    super();
    this.transaction = transaction;
    this.address = address;
    this.amount = amount;
  }

  public MoneroTransaction getTransaction() {
    return transaction;
  }

  public void setTransaction(MoneroTransaction transaction) {
    this.transaction = transaction;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public BigInteger getAmount() {
    return amount;
  }

  public void setAmount(BigInteger amount) {
    this.amount = amount;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((address == null) ? 0 : address.hashCode());
    result = prime * result + ((amount == null) ? 0 : amount.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MoneroPayment other = (MoneroPayment) obj;
    if (address == null) {
      if (other.address != null) return false;
    } else if (!address.equals(other.address)) return false;
    if (amount == null) {
      if (other.amount != null) return false;
    } else if (!amount.equals(other.amount)) return false;
    return true;
  }
}
