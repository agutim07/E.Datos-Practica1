package ule.edi.event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.*;

import ule.edi.model.*;
import ule.edi.model.Configuration.Type;

public class EventArrayImplTests {

	private DateFormat dformat = null;
	private EventArrayImpl e;
	private EventArrayImpl x;
	
	private Date parseLocalDate(String spec) throws ParseException {
        return dformat.parse(spec);
	}

	public EventArrayImplTests() {
		
		dformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}
	
	@Before
	public void testBefore() throws Exception{
	    e = new EventArrayImpl("The Fabulous Five", parseLocalDate("24/02/2018 17:00:00"), 110);

	}
	
	@Before
	public void testBefore2() throws Exception{
		x = new EventArrayImpl("Mystic Mac", parseLocalDate("24/02/2018 18:00:00"), 5, 100.0, (byte)50);
	}
	
	@Test
	public void testEventoVacio() throws Exception {
		
	    Assert.assertTrue(e.getNumberOfAvailableSeats()==110);
	    Assert.assertEquals(e.getNumberOfAvailableSeats(), 110);
	    Assert.assertEquals(e.getNumberOfAttendingAdults(), 0);
	}
	
	@Test
	public void testSellSeat1Adult() throws Exception{
		
			
	    Assert.assertEquals(e.getNumberOfAttendingAdults(), 0);
		Assert.assertTrue(e.sellSeat(1, new Person("10203040A","Alice", 34),false));	//venta normal
	    Assert.assertEquals(e.getNumberOfAttendingAdults(), 1);  
	    Assert.assertEquals(e.getNumberOfNormalSaleSeats(), 1);
	  
	}
	

	
	@Test
	public void testgetCollection() throws Exception{
		Event  ep = new EventArrayImpl("The Fabulous Five", parseLocalDate("24/02/2018 17:00:00"), 4);
		Assert.assertEquals(ep.sellSeat(1, new Person("1010", "AA", 10), true),true);
		Assert.assertTrue(ep.getCollectionEvent()==75);					
	}
	
	@Test
	public void testBasicMethods() throws Exception{
		Assert.assertEquals(x.getName(), "Mystic Mac");
		Assert.assertEquals(x.getDateEvent(), parseLocalDate("24/02/2018 18:00:00"));	
		Assert.assertTrue(x.getPrice()==100);
		Assert.assertTrue(x.getDiscountAdvanceSale()==50);
		Assert.assertTrue(x.getNumberOfSeats()==5);
	}
	
	@Test
	public void testRightRefund() throws Exception{
		Person pepe = new Person("Pepe","1010", 20);
		x.sellSeat(1, pepe, false);
		Assert.assertEquals(x.refundSeat(1), pepe);
		x.refundSeat(1);
		Assert.assertEquals(x.getSeat(1), null);
	}
	
	@Test
	public void testWrongRefund() throws Exception{
		Assert.assertEquals(x.refundSeat(-1), null);
		Assert.assertEquals(x.refundSeat(10), null);
	}
	
	@Test
	public void testWrongSell() throws Exception{
		Person pepe = new Person("Pepe","1010", 20);
		Assert.assertEquals(x.sellSeat(-1, pepe, true), false);
		Assert.assertEquals(x.sellSeat(20, pepe, true), false);
		x.sellSeat(1, pepe, true);
		Assert.assertEquals(x.sellSeat(1, new Person("Jimmy", "1010", 10), true), false);
	}
	
	@Test
	public void testWrongGetSeat() throws Exception{
		Assert.assertEquals(x.getSeat(-1), null);
		Assert.assertEquals(x.getSeat(10), null);
		Assert.assertEquals(x.getSeat(1), null);
	}
	
	@Test
	public void testRightGetSeat() throws Exception{
		x.sellSeat(1, new Person("Pepe","1010", 20), true);
		StringBuffer output = new StringBuffer();
		StringBuffer output2 = new StringBuffer();
		output2.append(x.getSeat(1));
		output.append("{'Event':'Mystic Mac', 'Holder':{ NIF: 1010  Name : Pepe, Age:20}, 'Price':50.0}");
		Assert.assertEquals(output2.toString(), output.toString());
	}
	
	@Test
	public void testArentChildrens() throws Exception{
		x.sellSeat(1, new Person("Pepe","1010", 20), true);
		Assert.assertTrue(x.getNumberOfAttendingChildren()==0);
	}
	
	@Test
	public void testAreChildrens() throws Exception{
		x.sellSeat(2, new Person("Kimono","1020", 10), true);
		Assert.assertTrue(x.getNumberOfAttendingChildren()==1);
	}
	
	@Test
	public void testArentAdults() throws Exception{
		x.sellSeat(1, new Person("Pepe","1010", 10), true);
		x.sellSeat(3, new Person("Jimmy","1010", 80), true);
		Assert.assertTrue(x.getNumberOfAttendingAdults()==0);
	}
	
	@Test
	public void testAreAdults() throws Exception{
		x.sellSeat(2, new Person("Kimono","1020", 20), true);
		Assert.assertTrue(x.getNumberOfAttendingAdults()==1);
	}
	
	@Test
	public void testArentEderly() throws Exception{
		x.sellSeat(1, new Person("Pepe","1010", 20), true);
		Assert.assertTrue(x.getNumberOfAttendingElderlyPeople()==0);
	}
	
	@Test
	public void testAreEderly() throws Exception{
		x.sellSeat(2, new Person("John","1030", 70), true);
		Assert.assertTrue(x.getNumberOfAttendingElderlyPeople()==1);
	}
	
	@Test
	public void testGetPriceDifferentEvent() throws Exception{
		Seat seat = new Seat(e, 1, Configuration.Type.NORMAL, new Person("Pepe","1010", 20));
		Assert.assertTrue(x.getPrice(seat) == 0);
	}
	
	@Test
	public void testGetPrice() throws Exception{
		Seat seat = new Seat(x, 1, Configuration.Type.NORMAL, new Person("Pepe","1010", 20));
		Assert.assertTrue(x.getPrice(seat) == 100);
		seat = new Seat(x, 1, Configuration.Type.ADVANCE_SALE, new Person("Pepe","1010", 20));
		Assert.assertTrue(x.getPrice(seat) == 50);
	}
	
	@Test
	public void testGetPosPersonWrong() throws Exception{
		Person pepe = new Person("Pepe","1010", 20);
		Assert.assertTrue(x.getPosPerson(pepe)==-1);
		x.sellSeat(2, new Person("Kimono","1020", 10), true);
		Assert.assertTrue(x.getPosPerson(pepe)==-1);
	}
	
	@Test
	public void testGetPosPerson() throws Exception{
		Person pepe = new Person("Pepe","1010", 20);
		x.sellSeat(3, pepe, true);
		Assert.assertTrue(x.getPosPerson(pepe)==3);
	}
	
	@Test
	public void testAdvanceSaleFalse() throws Exception{
		Person pepe = new Person("Pepe","1010", 20);
		x.sellSeat(2, pepe, false);
		Assert.assertEquals(x.isAdvanceSale(pepe), false);
	}
	
	@Test
	public void testAdvanceSale() throws Exception{
		Person pepe = new Person("Pepe","1010", 20);
		x.sellSeat(2, pepe, true);
		Assert.assertEquals(x.isAdvanceSale(pepe), true);
	}
	
	@Test
	public void testConsecutiveSeats() throws Exception{
		Assert.assertTrue(x.getMaxNumberConsecutiveSeats()==5);
		x.sellSeat(2, new Person("Kimono","1020", 10), true);
		x.sellSeat(3, new Person("Kimono","1020", 10), true);
		Assert.assertTrue(x.getMaxNumberConsecutiveSeats()==2);
	}
	
	@Test
	public void testConsecutiveSeats2() throws Exception{
		Assert.assertTrue(e.getMaxNumberConsecutiveSeats()==110);
		e.sellSeat(100, new Person("Kimono","1020", 10), true);
		Assert.assertTrue(e.getMaxNumberConsecutiveSeats()==99);
	}
	
	@Test
	public void testSoldSeats() throws Exception{
		Assert.assertTrue(x.getNumberOfSoldSeats()==0);
		x.sellSeat(2, new Person("Kimono","1020", 10), true);
		x.sellSeat(3, new Person("Kimono","1020", 10), true);
		Assert.assertTrue(x.getNumberOfSoldSeats()==2);
	}
	
	@Test
	public void testAdvanceNumberSells() throws Exception{
		Assert.assertTrue(x.getNumberOfAdvanceSaleSeats()==0);
		x.sellSeat(2, new Person("Kimono","1020", 10), false);
		x.sellSeat(3, new Person("Kimono","1020", 10), true);
		Assert.assertTrue(x.getNumberOfAdvanceSaleSeats()==1);
	}
	
	@Test
	public void testNormalNumberSells() throws Exception{
		Assert.assertTrue(x.getNumberOfNormalSaleSeats()==0);
		x.sellSeat(2, new Person("Kimono","1020", 10), false);
		x.sellSeat(3, new Person("Kimono","1020", 10), true);
		Assert.assertTrue(x.getNumberOfNormalSaleSeats()==1);
	}
	
	@Test
	public void testAvailableList() throws Exception{
		List<Integer> lista = new ArrayList<Integer>();
		lista.add(1);
		lista.add(2);
		lista.add(5);
		x.sellSeat(3, new Person("Kimono","1020", 10), false);
		x.sellSeat(4, new Person("Kimono","1020", 10), true);
		Assert.assertEquals(x.getAvailableSeatsList(), lista);
	}
	
	@Test
	public void testAdvanceList() throws Exception{
		List<Integer> lista = new ArrayList<Integer>();
		lista.add(3);
		x.sellSeat(1, new Person("Kimono","1020", 10), false);
		x.sellSeat(3, new Person("Kimono","1020", 10), true);
		Assert.assertEquals(x.getAdvanceSaleSeatsList(), lista);
	}
	
}
