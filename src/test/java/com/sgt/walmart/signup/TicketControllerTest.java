package com.sgt.walmart.signup;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;

import com.sgt.walmart.config.WebAppConfigurationAware;

public class TicketControllerTest extends WebAppConfigurationAware {
    @Test
    public void testDisplaySeatCount() throws Exception {
        mockMvc.perform(get("/seatCount"))
                .andExpect(view().name("home/seatCount"))
                .andExpect(content().string(
                        allOf(
                                containsString("Find Tickets |"),
                                containsString("Get Count")
                        ))
                );
    }
    
    @Test
    public void testDisplay() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(view().name("home/seatCount"))
                .andExpect(content().string(
                        allOf(
                                containsString("Find Tickets |"),
                                containsString("Get Count")
                        ))
                );
    }

    @Test
    public void testDisplayHoldSeats() throws Exception {
        mockMvc.perform(get("/holdSeats"))
                .andExpect(view().name("home/holdSeats"))
                .andExpect(content().string(
                        allOf(
                                containsString(" | Hold Tickets"),
                                containsString("Select lowest Level")
                        ))
                );
    }

    @Test
    public void testDisplayReserve() throws Exception {
        mockMvc.perform(get("/reserve"))
                .andExpect(view().name("home/reserve"))
                .andExpect(content().string(
                        allOf(
                                containsString("Reserve")
                        ))
                );
    }
    
}