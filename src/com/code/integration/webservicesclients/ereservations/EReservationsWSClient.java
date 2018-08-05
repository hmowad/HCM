package com.code.integration.webservicesclients.ereservations;

import java.util.ArrayList;
import java.util.List;

import com.code.integration.exceptions.ClientIntegrationException;
import com.code.integration.responses.ereservations.AirportData;
import com.code.integration.responses.ereservations.BookingData;
import com.code.integration.responses.ereservations.LegData;
import com.code.integration.responses.ereservations.MealData;

public class EReservationsWSClient {

    public static List<AirportData> getAirports(String socialId) throws ClientIntegrationException {

	EReservationWSService eReservationWSService = new EReservationWSService();
	EReservationWS eReservationWS = eReservationWSService.getEReservationWSPort();

	AirportsResult airportsResult = eReservationWS.getAirports(socialId);
	if (!airportsResult.getStatus().value().equals(EReservationWSStatusEnum.SUCCEEDED.value())) {
	    System.out.println("********** eReservations WS Call : getAirports() " + airportsResult.getStatus().value() + " " + airportsResult.getMessage() + " **********");
	    throw new ClientIntegrationException(airportsResult.getMessage());
	}

	List<AirportData> airports = new ArrayList<AirportData>();
	List<Airport> airportsList = airportsResult.getAirportsList();
	for (Airport airport : airportsList) {
	    AirportData airportData = new AirportData();
	    airportData.setId(airport.getId());
	    airportData.setCode(airport.getCode());
	    airportData.setName(airport.getDescription());
	    airports.add(airportData);
	}

	return airports;
    }

    public static List<MealData> getMeals() {

	EReservationWSService eReservationWSService = new EReservationWSService();
	EReservationWS eReservationWS = eReservationWSService.getEReservationWSPort();

	List<MealResult> mealsList = eReservationWS.getMeals();

	List<MealData> meals = new ArrayList<MealData>();
	for (MealResult meal : mealsList) {
	    MealData mealData = new MealData();
	    mealData.setCode(meal.getCode().value());
	    mealData.setValue(meal.getValue());
	    meals.add(mealData);
	}

	return meals;
    }

    public static List<BookingData> getBookings(String socialId, boolean previousBookingsFlag) throws ClientIntegrationException {

	EReservationWSService eReservationWSService = new EReservationWSService();
	EReservationWS eReservationWS = eReservationWSService.getEReservationWSPort();

	BookingsResult bookingsResult = eReservationWS.getBookings(socialId, previousBookingsFlag);

	if (!bookingsResult.getStatus().value().equals(EReservationWSStatusEnum.SUCCEEDED.value())) {
	    System.out.println("********** eReservations WS Call : getBookings() " + bookingsResult.getStatus().value() + " " + bookingsResult.getMessage() + " **********");
	    throw new ClientIntegrationException(bookingsResult.getMessage());
	}

	List<BookingData> bookings = new ArrayList<BookingData>();
	List<ReservationTickets> reservationTicketsList = bookingsResult.getBookingsList();
	for (ReservationTickets reservationTickets : reservationTicketsList) {
	    List<TicketData> ticketDataList = reservationTickets.getReservationTickets();
	    for (TicketData ticketData : ticketDataList) {
		BookingData bookingData = new BookingData();

		bookingData.setFlightCode(ticketData.getFlightCode());
		bookingData.setFlightDateString(ticketData.getInstanceDateString());
		bookingData.setDepartureTime(ticketData.getDepartureTime());
		bookingData.setFromAirportName(ticketData.getAirportFromDesc());
		bookingData.setToAirportName(ticketData.getAirportToDesc());
		bookingData.setBookingStatusDescription(ticketData.getReservationStatusArabicDesc());
		bookingData.setBookingReference(ticketData.getReservationNumber());

		bookings.add(bookingData);
	    }
	}

	return bookings;
    }

    public static Object[] getFlights(String socialId, long fromAirportId, long toAirportId, String departureDateString, String returnDateString) throws ClientIntegrationException {

	Object[] result = new Object[3];

	EReservationWSService eReservationWSService = new EReservationWSService();
	EReservationWS eReservationWS = eReservationWSService.getEReservationWSPort();

	FlightsResult flightsResult = eReservationWS.getFlights(socialId, fromAirportId, toAirportId, departureDateString, returnDateString);
	if (!flightsResult.getStatus().value().equals(EReservationWSStatusEnum.SUCCEEDED.value())) {
	    System.out.println("********** eReservations WS Call : getFlights() " + flightsResult.getStatus().value() + " " + flightsResult.getMessage() + " **********");
	    throw new ClientIntegrationException(flightsResult.getMessage());
	}

	List<LegData> goingLegs = new ArrayList<LegData>();
	FlightSearchResult flightSearchResult = flightsResult.getFlightSearchResult();
	List<LegInstancesData> legInstancesDataList = flightSearchResult.getLegInstancesDataList();
	for (LegInstancesData legInstancesData : legInstancesDataList) {
	    LegData legData = new LegData();

	    legData.setId(legInstancesData.getId());
	    legData.setDepartureDateString(legInstancesData.getInstanceDateString());
	    legData.setDepartureTime(legInstancesData.getDepartureTime());
	    legData.setFromAirportName(legInstancesData.getFromAirport());
	    legData.setToAirportName(legInstancesData.getToAirport());

	    goingLegs.add(legData);
	}

	List<LegData> returnLegs = new ArrayList<LegData>();
	flightSearchResult = flightsResult.getFlightSearchResultReturn();
	legInstancesDataList = flightSearchResult.getLegInstancesDataList();
	for (LegInstancesData legInstancesData : legInstancesDataList) {
	    LegData legData = new LegData();

	    legData.setId(legInstancesData.getId());
	    legData.setDepartureDateString(legInstancesData.getInstanceDateString());
	    legData.setDepartureTime(legInstancesData.getDepartureTime());
	    legData.setFromAirportName(legInstancesData.getFromAirport());
	    legData.setToAirportName(legInstancesData.getToAirport());

	    returnLegs.add(legData);
	}

	result[0] = goingLegs;
	result[1] = returnLegs;
	result[2] = flightsResult.isWaitingFlight();

	return result;
    }

    public static void bookFlight(String socialId, String goingLegsIds, String returnLegsIds, boolean waitingFlightFlag, String contactPhone, String contactEmail,
	    String mealCode, boolean needTransportationFlag) throws ClientIntegrationException {

	EReservationWSService eReservationWSService = new EReservationWSService();
	EReservationWS eReservationWS = eReservationWSService.getEReservationWSPort();

	BookingsResult bookingsResult = eReservationWS.bookFlight(socialId, contactPhone, contactEmail, MealEnum.fromValue(mealCode),
		needTransportationFlag ? TransportationEnum.YES : TransportationEnum.NO, goingLegsIds, returnLegsIds, waitingFlightFlag);
	if (!bookingsResult.getStatus().value().equals(EReservationWSStatusEnum.SUCCEEDED.value())) {
	    System.out.println("********** eReservations WS Call : bookFlight() " + bookingsResult.getStatus().value() + " " + bookingsResult.getMessage() + " **********");
	    throw new ClientIntegrationException(bookingsResult.getMessage());
	}
    }

}
