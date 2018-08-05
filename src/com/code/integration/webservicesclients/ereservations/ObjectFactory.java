
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.code.integration.webservicesclients.ereservations package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetFlights_QNAME = new QName("http://webservice.services.code.com/", "getFlights");
    private final static QName _BookFlightResponse_QNAME = new QName("http://webservice.services.code.com/", "bookFlightResponse");
    private final static QName _GetBookings_QNAME = new QName("http://webservice.services.code.com/", "getBookings");
    private final static QName _GetMeals_QNAME = new QName("http://webservice.services.code.com/", "getMeals");
    private final static QName _CancelBooking_QNAME = new QName("http://webservice.services.code.com/", "cancelBooking");
    private final static QName _BookFlight_QNAME = new QName("http://webservice.services.code.com/", "bookFlight");
    private final static QName _GetFlightsResponse_QNAME = new QName("http://webservice.services.code.com/", "getFlightsResponse");
    private final static QName _CancelBookingResponse_QNAME = new QName("http://webservice.services.code.com/", "cancelBookingResponse");
    private final static QName _GetMealsResponse_QNAME = new QName("http://webservice.services.code.com/", "getMealsResponse");
    private final static QName _GetAirportsResponse_QNAME = new QName("http://webservice.services.code.com/", "getAirportsResponse");
    private final static QName _GetAirports_QNAME = new QName("http://webservice.services.code.com/", "getAirports");
    private final static QName _GetBookingsResponse_QNAME = new QName("http://webservice.services.code.com/", "getBookingsResponse");
    private final static QName _GetFlightsReturnDate_QNAME = new QName("", "returnDate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.code.integration.webservicesclients.ereservations
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BookFlightResponse }
     * 
     */
    public BookFlightResponse createBookFlightResponse() {
        return new BookFlightResponse();
    }

    /**
     * Create an instance of {@link GetBookings }
     * 
     */
    public GetBookings createGetBookings() {
        return new GetBookings();
    }

    /**
     * Create an instance of {@link GetFlights }
     * 
     */
    public GetFlights createGetFlights() {
        return new GetFlights();
    }

    /**
     * Create an instance of {@link BookFlight }
     * 
     */
    public BookFlight createBookFlight() {
        return new BookFlight();
    }

    /**
     * Create an instance of {@link CancelBooking }
     * 
     */
    public CancelBooking createCancelBooking() {
        return new CancelBooking();
    }

    /**
     * Create an instance of {@link GetMeals }
     * 
     */
    public GetMeals createGetMeals() {
        return new GetMeals();
    }

    /**
     * Create an instance of {@link GetAirportsResponse }
     * 
     */
    public GetAirportsResponse createGetAirportsResponse() {
        return new GetAirportsResponse();
    }

    /**
     * Create an instance of {@link CancelBookingResponse }
     * 
     */
    public CancelBookingResponse createCancelBookingResponse() {
        return new CancelBookingResponse();
    }

    /**
     * Create an instance of {@link GetMealsResponse }
     * 
     */
    public GetMealsResponse createGetMealsResponse() {
        return new GetMealsResponse();
    }

    /**
     * Create an instance of {@link GetFlightsResponse }
     * 
     */
    public GetFlightsResponse createGetFlightsResponse() {
        return new GetFlightsResponse();
    }

    /**
     * Create an instance of {@link GetAirports }
     * 
     */
    public GetAirports createGetAirports() {
        return new GetAirports();
    }

    /**
     * Create an instance of {@link GetBookingsResponse }
     * 
     */
    public GetBookingsResponse createGetBookingsResponse() {
        return new GetBookingsResponse();
    }

    /**
     * Create an instance of {@link LegInstancesData }
     * 
     */
    public LegInstancesData createLegInstancesData() {
        return new LegInstancesData();
    }

    /**
     * Create an instance of {@link TicketData }
     * 
     */
    public TicketData createTicketData() {
        return new TicketData();
    }

    /**
     * Create an instance of {@link BookingsResult }
     * 
     */
    public BookingsResult createBookingsResult() {
        return new BookingsResult();
    }

    /**
     * Create an instance of {@link MealResult }
     * 
     */
    public MealResult createMealResult() {
        return new MealResult();
    }

    /**
     * Create an instance of {@link ReservationTickets }
     * 
     */
    public ReservationTickets createReservationTickets() {
        return new ReservationTickets();
    }

    /**
     * Create an instance of {@link LegInstance }
     * 
     */
    public LegInstance createLegInstance() {
        return new LegInstance();
    }

    /**
     * Create an instance of {@link AirportsResult }
     * 
     */
    public AirportsResult createAirportsResult() {
        return new AirportsResult();
    }

    /**
     * Create an instance of {@link Airport }
     * 
     */
    public Airport createAirport() {
        return new Airport();
    }

    /**
     * Create an instance of {@link FlightsResult }
     * 
     */
    public FlightsResult createFlightsResult() {
        return new FlightsResult();
    }

    /**
     * Create an instance of {@link FlightSearchResult }
     * 
     */
    public FlightSearchResult createFlightSearchResult() {
        return new FlightSearchResult();
    }

    /**
     * Create an instance of {@link CancelResult }
     * 
     */
    public CancelResult createCancelResult() {
        return new CancelResult();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFlights }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getFlights")
    public JAXBElement<GetFlights> createGetFlights(GetFlights value) {
        return new JAXBElement<GetFlights>(_GetFlights_QNAME, GetFlights.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BookFlightResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "bookFlightResponse")
    public JAXBElement<BookFlightResponse> createBookFlightResponse(BookFlightResponse value) {
        return new JAXBElement<BookFlightResponse>(_BookFlightResponse_QNAME, BookFlightResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBookings }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getBookings")
    public JAXBElement<GetBookings> createGetBookings(GetBookings value) {
        return new JAXBElement<GetBookings>(_GetBookings_QNAME, GetBookings.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMeals }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getMeals")
    public JAXBElement<GetMeals> createGetMeals(GetMeals value) {
        return new JAXBElement<GetMeals>(_GetMeals_QNAME, GetMeals.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelBooking }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "cancelBooking")
    public JAXBElement<CancelBooking> createCancelBooking(CancelBooking value) {
        return new JAXBElement<CancelBooking>(_CancelBooking_QNAME, CancelBooking.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BookFlight }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "bookFlight")
    public JAXBElement<BookFlight> createBookFlight(BookFlight value) {
        return new JAXBElement<BookFlight>(_BookFlight_QNAME, BookFlight.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFlightsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getFlightsResponse")
    public JAXBElement<GetFlightsResponse> createGetFlightsResponse(GetFlightsResponse value) {
        return new JAXBElement<GetFlightsResponse>(_GetFlightsResponse_QNAME, GetFlightsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelBookingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "cancelBookingResponse")
    public JAXBElement<CancelBookingResponse> createCancelBookingResponse(CancelBookingResponse value) {
        return new JAXBElement<CancelBookingResponse>(_CancelBookingResponse_QNAME, CancelBookingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMealsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getMealsResponse")
    public JAXBElement<GetMealsResponse> createGetMealsResponse(GetMealsResponse value) {
        return new JAXBElement<GetMealsResponse>(_GetMealsResponse_QNAME, GetMealsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAirportsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getAirportsResponse")
    public JAXBElement<GetAirportsResponse> createGetAirportsResponse(GetAirportsResponse value) {
        return new JAXBElement<GetAirportsResponse>(_GetAirportsResponse_QNAME, GetAirportsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAirports }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getAirports")
    public JAXBElement<GetAirports> createGetAirports(GetAirports value) {
        return new JAXBElement<GetAirports>(_GetAirports_QNAME, GetAirports.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBookingsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.services.code.com/", name = "getBookingsResponse")
    public JAXBElement<GetBookingsResponse> createGetBookingsResponse(GetBookingsResponse value) {
        return new JAXBElement<GetBookingsResponse>(_GetBookingsResponse_QNAME, GetBookingsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "returnDate", scope = GetFlights.class)
    public JAXBElement<String> createGetFlightsReturnDate(String value) {
        return new JAXBElement<String>(_GetFlightsReturnDate_QNAME, String.class, GetFlights.class, value);
    }

}
