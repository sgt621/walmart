package com.sgt.walmart.account;

import java.util.Optional;

public class HoldForm {

	private Integer numberTickets;
	private Optional<Integer> seatingLevel;
	private String maxLevel;
	private String minLevel;
	private String email;
	
	public Integer getNumberTickets() {
		return numberTickets;
	}

	public void setNumberTickets(Integer numberTickets) {
		this.numberTickets = numberTickets;
	}

	public Optional<Integer> getSeatingLevel() {
		return seatingLevel;
	}

	public void setSeatingLevel(Optional<Integer> seatingLevel) {
		this.seatingLevel = seatingLevel;
	}

	public String getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(String maxLevel) {
		this.maxLevel = maxLevel;
	}

	public String getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(String minLevel) {
		this.minLevel = minLevel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
