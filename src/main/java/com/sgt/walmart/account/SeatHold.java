package com.sgt.walmart.account;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "seat_hold")
public class SeatHold implements java.io.Serializable {

	@Id
	@GeneratedValue
	private Long seq;

	@Column
	private Integer level;

	@Column
	private Integer num;
	
	@Column
	private String email;
	
	@Column
	private Timestamp expirationDate;
	
	@Column
	private String confirmation;
	
	@Column
	private Integer seatHoldId;
	
	@Transient
	private String expirationString;
	
    protected SeatHold() {

	}
	
	public SeatHold(Long seq, Integer level, Integer num, String email, 
					Timestamp expirationDate, String confirmation, Integer seatHoldId) {
		super();
		this.seq = seq;
		this.level = level;
		this.num = num;
		this.email = email;
		this.expirationDate = expirationDate;
		this.confirmation = confirmation;
		this.seatHoldId = seatHoldId;
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	public Integer getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(Integer seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Timestamp getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Timestamp expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getExpirationString() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return df.format(expirationDate);
	}

}
