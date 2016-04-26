package com.sgt.walmart.account;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class TicketController {
	@Autowired
	TicketService ticketService;
	
	public TicketService getTicketService() {
		return ticketService;
	}

	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "home/seatCount";
	}

	@RequestMapping(value = "/seatCount", method = RequestMethod.GET)
	public String seatCount() {
		return "home/seatCount";
	}

	@RequestMapping(value = "/holdSeats", method = RequestMethod.GET)
	public String holdSeats() {
		return "home/holdSeats";
	}

	@RequestMapping(value = "/reserve", method = RequestMethod.GET)
	public String reserve(ModelMap model) {
		return "home/reserve";
	}

	private Optional<Integer> convertLevel(String param) {
		Optional<Integer> level = Optional.ofNullable(-1);
		if (param!=null) {
			if (param.equals("orchestra"))
				level = Optional.ofNullable(1);
			else if (param.equals("main"))
				level = Optional.ofNullable(2);
			else if (param.equals("balcony1"))
				level = Optional.ofNullable(3);
			else if (param.equals("balcony2"))
				level = Optional.ofNullable(4);
		}
		return level;
	}
	  
	@RequestMapping(value = "/seatCount", method = RequestMethod.POST, params="getCount")
	public String getCount(@Valid @ModelAttribute HoldForm holdForm, Errors errors, RedirectAttributes ra, ModelMap model) {
		model.addAttribute("count", ticketService.numSeatsAvailable((holdForm.getSeatingLevel().get()>0?holdForm.getSeatingLevel():null)));
		if (holdForm.getSeatingLevel().isPresent() && holdForm.getSeatingLevel().get()>0)
			model.addAttribute("level", "the " + levelToString(holdForm.getSeatingLevel().get()) + " level.");
		else model.addAttribute("level", "all levels.");
		return "home/seatCount";
	}

	@RequestMapping(value = "/seatCount", method = RequestMethod.POST, params="holdTix")
	public String returnHoldSeats(@Valid @ModelAttribute HoldForm holdForm, Errors errors, RedirectAttributes ra, ModelMap model) {
		return holdSeats();
	}

   @RequestMapping(value = "/holdTix", method = RequestMethod.POST)
	public String holdTix(@Valid @ModelAttribute HoldForm holdForm, Errors errors, RedirectAttributes ra, ModelMap model) {
	   boolean invalid = false;
	   StringBuilder validationMsg = new StringBuilder("");
	   
	   if (errors.hasErrors()) {
			return "home/error";
		}

		if (holdForm.getEmail()==null || holdForm.getEmail().trim().length()==0) {
			validationMsg.append("Email is required.");
			invalid = true;
		}
		if (holdForm.getNumberTickets()==null || holdForm.getNumberTickets().equals(0)) {
			validationMsg.append("Number of Tickets is required.");
			invalid = true;
		}
		
		if (invalid) {
			model.addAttribute("validationMsg", validationMsg);
			return "home/holdSeats";			
		}
			
		//convert levels from string to optional<integer>
		String min = holdForm.getMinLevel();
		String max = holdForm.getMaxLevel();
		Optional<Integer> minLevel = convertLevel(min);
		Optional<Integer> maxLevel = convertLevel(max);

		//hold seats
		SeatHold seatHold = ticketService.findAndHoldSeats(holdForm.getNumberTickets(), minLevel, maxLevel, holdForm.getEmail());
		
		//check for error codes
		if (seatHold.getSeq()==-2) {
			model.addAttribute("validationMsg", "Please select a broader venue level or break up your seats by venue.");
			return "home/holdSeats";
		}
		if (seatHold.getSeq()==-1) {
			model.addAttribute("validationMsg", "Please select fewer seats.");
			return "home/holdSeats";
		}
		else if (seatHold.getSeq()==0) {
			return "home/soldOut";
		}
		
		//if no error, process accordingly
		model.addAttribute("expirationString", seatHold.getExpirationString());	
		model.addAttribute("num", seatHold.getNum());
		model.addAttribute("level", levelToString(seatHold.getLevel()));
		model.addAttribute("seatHoldId", seatHold.getSeq());
		
		return "home/holdSeatsConfirm";
	}
   
   @RequestMapping(value = "/reserveTix", method = RequestMethod.POST)
	public String reserveTix(@Valid @ModelAttribute ReserveForm reserveForm, Errors errors, RedirectAttributes ra, ModelMap model) {
	   boolean invalid = false;
	   StringBuilder validationMsg = new StringBuilder("");
	   
	   if (errors.hasErrors()) {
			return "home/error";
		}

		if (reserveForm.getEmail()==null || reserveForm.getEmail().trim().length()==0) {
			validationMsg.append("Email is required.");
			invalid = true;
		}
		if (reserveForm.getSeatHoldId()==0) {
			validationMsg.append("Seat Hold Number is required.");
			invalid = true;
		}
		
		if (invalid) {
			model.addAttribute("validationMsg", validationMsg);
			return "home/reserve";
		}
			
		String msg = ticketService.reserveSeats(reserveForm.getSeatHoldId(), reserveForm.getEmail());
		if ("error".equals(msg)) {
			model.addAttribute("validationMsg", "Your held seat was not located.");
			return "home/reserve";
		}
		if ("expired".equals(msg)) 
			return "home/expired";
		//if no errors, process accordingly
		else {
			model.addAttribute("msg", msg);
			return "home/confirmation";
		}	
	}
   
	private String levelToString(Integer param) {
		String level = "";
		if (param!=null) {
			if (param.equals(1))
				level = "orchestra";
			else if (param.equals(2))
				level = "main";
			else if (param.equals(3))
				level = "balcony1";
			else if (param.equals(4))
				level = "balcony2";
		}
		return level;
	}
}