package com.sgt.walmart.account;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

/**
 * @author Samira
 *
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TicketServiceImpl implements TicketService {
	static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
	private final static Integer TIMER = 3;
	
	private Integer orchestraSeatsTotal = 25*50;
	private Integer mainSeatsTotal = 20*100;
	private Integer balcony1SeatsTotal = 15*100;
	private Integer balcony2SeatsTotal = 15*100;
	
	@Autowired
	SeatHoldRepository seatHoldRepository;
	
	public Integer getOrchestraSeatsTotal() {
		return orchestraSeatsTotal;
	}

	public void setOrchestraSeatsTotal(Integer orchestraSeatsTotal) {
		this.orchestraSeatsTotal = orchestraSeatsTotal;
	}

	public Integer getMainSeatsTotal() {
		return mainSeatsTotal;
	}

	public void setMainSeatsTotal(Integer mainSeatsTotal) {
		this.mainSeatsTotal = mainSeatsTotal;
	}

	public Integer getBalcony1SeatsTotal() {
		return balcony1SeatsTotal;
	}

	public void setBalcony1SeatsTotal(Integer balcony1SeatsTotal) {
		this.balcony1SeatsTotal = balcony1SeatsTotal;
	}

	public Integer getBalcony2SeatsTotal() {
		return balcony2SeatsTotal;
	}

	public void setBalcony2SeatsTotal(Integer balcony2SeatsTotal) {
		this.balcony2SeatsTotal = balcony2SeatsTotal;
	}

	public SeatHoldRepository getSeatHoldRepository() {
		return seatHoldRepository;
	}

	public void setSeatHoldRepository(SeatHoldRepository seatHoldRepository) {
		this.seatHoldRepository = seatHoldRepository;
	}

	public int numSeatsAvailable(Optional<Integer> venueLevel) {
		//delete all expired seat holds
		seatHoldRepository.deleteExpired();
		int count = 0;
		if (venueLevel!=null) {
			if (venueLevel.get()>0 && venueLevel.get()<5) {
				//find all reserved and held seats by level
				List<SeatHold> seatsHeld = seatHoldRepository.findAllByLevel(venueLevel.get());
				for (SeatHold temp : seatsHeld) {
					count += temp.getNum();
				}
				if (venueLevel.get().equals(1)) 
					return orchestraSeatsTotal - count;
				else if (venueLevel.get().equals(2)) 
					return mainSeatsTotal - count;
				else if (venueLevel.get().equals(3)) 
					return balcony1SeatsTotal - count;
				else if (venueLevel.get().equals(4)) 
					return balcony2SeatsTotal - count;
			}
		}
		//if looking through all levels, find all seats, then compute accordingly
		else {
			List<SeatHold> seatsHeld = seatHoldRepository.findAll();
			for (SeatHold temp : seatsHeld) {
				count += temp.getNum();
			}
			return (int) (orchestraSeatsTotal + mainSeatsTotal + balcony1SeatsTotal + balcony2SeatsTotal - count);
		}
		//if any other value is passed in, return 0
		return 0;
	}
	
	private Optional<Integer> findHighestLevel(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel) {
		//find best available seats within chosen bounds
		Optional<Integer> level = (maxLevel.get()>0?maxLevel:Optional.of(1));
		if (minLevel.get().equals(-1)) {
			minLevel = Optional.of(4);
		}
		boolean found = false;
		while (level.get() <= minLevel.get() && !found) {
			if (numSeatsAvailable(level) < numSeats)
				level = Optional.of(level.get() + 1);
			else found = true;
		}
		if (found)
			return level;
		
		//if venue is sold out, return 0
		if (numSeatsAvailable(null) < 1)
			level = Optional.of(0);
		
		//if venue number of seats is unavailable, return -1
		else if (numSeatsAvailable(null) < numSeats)
			level = Optional.of(-1);
		
		//if venue can accomodate on a different level, return -2
		else level = Optional.of(-2);
		
		return level;
	}

	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail) {
		SeatHold seatHold = new SeatHold();
		//check all parameters
		if (numSeats==0 || customerEmail==null)
			return seatHold;
		if (!validateEmail(customerEmail))
			return seatHold;
		if (minLevel.isPresent() && minLevel.get()==0)
			minLevel = null;
		if (maxLevel.isPresent() && maxLevel.get()==0)
			maxLevel = null;		
		//prepare to compute expiration date
		Calendar date = Calendar.getInstance();
		long t = date.getTimeInMillis();
		//find highest level
		Optional<Integer> level = findHighestLevel(numSeats, minLevel, maxLevel);
		if (level.isPresent() && level.get().intValue()>0) {
			seatHold.setExpirationDate(new java.sql.Timestamp(new Date(t + (TIMER * ONE_MINUTE_IN_MILLIS)).getTime()));
			seatHold.setNum(numSeats);
			seatHold.setLevel(level.get());
			seatHold.setEmail(customerEmail);
			return seatHoldRepository.save(seatHold);
		}
		//if there was a problem with the seat hold, forward error code to controller
		else if (level.isPresent() && level.get().intValue()==0) {
			seatHold.setSeq(0L);
		}
		else if (level.isPresent() && level.get().intValue()==-1) {
			seatHold.setSeq(-1L);
		}
		else if (level.isPresent() && level.get().intValue()==-2) {
			seatHold.setSeq(-2L);
		}
		return seatHold;
	}

	private boolean validateEmail(String customerEmail) {
		if (customerEmail!=null) {
			int dotIndex = customerEmail.lastIndexOf(".");
			int atIndex = customerEmail.lastIndexOf("@");
			if (dotIndex < 1 || atIndex < 1 || dotIndex < atIndex) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public String reserveSeats(int seatHoldId, String customerEmail) {
		Date today = new Date();
		if (seatHoldId==0 || !validateEmail(customerEmail)) {
			return "error"; 
		}
		SeatHold seatHold = seatHoldRepository.findOne(new Long(seatHoldId));
		if (seatHold==null) {
			return "error";
		}
		if (today.getTime()>seatHold.getExpirationDate().getTime()) {
			return "expired";
		}
		//if no errors or exceptions, remove the expiration date and set the confirmation number and commit
		seatHold.setExpirationDate(null);
		String confirmationNumber = (seatHoldId + 1000)/2 + "A";
		seatHold.setConfirmation(confirmationNumber);
		seatHold = seatHoldRepository.save(seatHold);
		
		return confirmationNumber;
	}
}
