package com.sgt.walmart.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

	@InjectMocks
	private TicketServiceImpl ticketService = new TicketServiceImpl();

	@Mock
	private SeatHoldRepository seatHoldRepositoryMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testNumSeatsAvailableAll() {
		Integer num = ticketService.numSeatsAvailable(null);
		assertThat(num).isEqualTo((ticketService.getOrchestraSeatsTotal()) + ticketService.getMainSeatsTotal() + ticketService.getBalcony1SeatsTotal() + ticketService.getBalcony2SeatsTotal());
	}
	
	@Test
	public void testNumSeatsAvailable0() {
		Integer num = ticketService.numSeatsAvailable(Optional.of(0));
		assertThat(num).isEqualTo(0);
	}
	
	@Test
	public void testNumSeatsAvailableOrchestra() {
		Integer num = ticketService.numSeatsAvailable(Optional.of(1));
		assertThat(num).isEqualTo((ticketService.getOrchestraSeatsTotal()));
	}

	@Test
	public void testNumSeatsAvailableMain() {
		Integer num = ticketService.numSeatsAvailable(Optional.of(2));
		assertThat(num).isEqualTo((ticketService.getMainSeatsTotal()));
	}

	@Test
	public void testNumSeatsAvailableBalcony1() {
		Integer num = ticketService.numSeatsAvailable(Optional.of(3));
		assertThat(num).isEqualTo((ticketService.getBalcony1SeatsTotal()));
	}

	@Test
	public void testNumSeatsAvailableBalcony2() {
		Integer num = ticketService.numSeatsAvailable(Optional.of(4));
		assertThat(num).isEqualTo((ticketService.getBalcony2SeatsTotal()));
	}

	@Test
	public void testNumSeatsAvailableError() {
		Integer num = ticketService.numSeatsAvailable(Optional.of(5));
		assertThat(num).isEqualTo(0);
	}
	
	@Test
	public void testFindAndHoldSeatsNoSeats() {
		SeatHold seatHold = ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(1), "test@test.com");
		assertThat(seatHold).isNotNull();
		assertThat(seatHold.getSeq()).isNull();
	}

	@Test
	public void testFindAndHoldSeatsInvalidEmail1() {
		SeatHold seatHold = ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(1), "@test.com");
		assertThat(seatHold).isNotNull();
		assertThat(seatHold.getSeq()).isNull();
	}

	@Test
	public void testFindAndHoldSeatsInvalidEmail2() {
		SeatHold seatHold = ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(1), ".com");
		assertThat(seatHold).isNotNull();
		assertThat(seatHold.getSeq()).isNull();
	}

	@Test
	public void testFindAndHoldSeatsNoEmail() {
		SeatHold seatHold = ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(1), null);
		assertThat(seatHold).isNotNull();
		assertThat(seatHold.getSeq()).isNull();
	}
	
	@Test
	public void testReserveSeatsNoTickets() {
		assertThat((ticketService.reserveSeats(0, "test@test.com"))).isEqualTo("error");
	}

	@Test
	public void reserveSeatsEmptyEmail() {
		assertThat((ticketService.reserveSeats(1, ""))).isEqualTo("error");
	}

	@Test
	public void reserveSeatsNullEmail() {
		assertThat((ticketService.reserveSeats(1, null))).isEqualTo("error");
	}
}