package ule.edi.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ule.edi.model.*;
import ule.edi.model.Configuration.Type;


public class EventArrayImpl implements Event {
	
	private String name;
	private Date eventDate;
	private int nSeats;
	
	private Double price;    // precio de entradas 
	private Byte discountAdvanceSale;   // descuento en venta anticipada (0..100)
   	
	private Seat[] seats;
		
	
	
   public EventArrayImpl(String name, Date date, int nSeats){
	   this.name=name;
	   this.eventDate=date;
	   this.nSeats=nSeats;
	   this.seats= new Seat[nSeats];
	   this.price=Configuration.DEFAULT_PRICE;
	   this.discountAdvanceSale=Configuration.DEFAULT_DISCOUNT;   
   }
   
   
   public EventArrayImpl(String name, Date date, int nSeats, Double price, Byte discount){
	   this.name=name;
	   this.eventDate=date;
	   this.nSeats=nSeats;
	   this.seats= new Seat[nSeats];
	   this.price=price;
	   this.discountAdvanceSale=discount; 
   }


@Override
public String getName() {
	return name;
}


@Override
public Date getDateEvent() {
	return eventDate;
}


@Override
public Double getPrice() {
	return price;
}


@Override
public Byte getDiscountAdvanceSale() {
	return discountAdvanceSale;
}


@Override
public int getNumberOfSoldSeats() {
	int sells = 0;
	for (int i = 0; i<nSeats; i++) {
		if (seats[i] != null) {
			sells++;
		}
	}
	return sells;
}


@Override
public int getNumberOfNormalSaleSeats() {
	int normal = 0;
	for (int i = 0; i<nSeats; i++) {
		if (seats[i]!=null) {
			if (seats[i].getType() == Configuration.Type.NORMAL) {
				normal++;
			}
		}
	}
	return normal;
}


@Override
public int getNumberOfAdvanceSaleSeats() {
	int advance = 0;
	for (int i = 0; i<nSeats; i++) {
		if (seats[i]!=null) {
			if (seats[i].getType() == Configuration.Type.ADVANCE_SALE) {
				advance++;
			}
		}
	}
	return advance;
}


@Override
public int getNumberOfSeats() {
	return nSeats;
}


@Override
public int getNumberOfAvailableSeats() {
	return nSeats-getNumberOfSoldSeats();
}


@Override
public Seat getSeat(int pos) {
	if (pos <= 0 || pos > seats.length-1) {
		return null;
	}
	if (seats[pos-1] == null) {
		return null;
	}
	return seats[pos-1];
}


@Override
public Person refundSeat(int pos) {
	if (pos <= 0 || pos > seats.length-1) {
		return null;
	}
	if (seats[pos-1] == null) {
		return null;
	}
	Person x = seats[pos-1].getHolder();
	seats[pos-1] = null;
	return x;
}


@Override
public boolean sellSeat(int pos, Person p, boolean advanceSale) {
	if (pos <= 0 || pos > seats.length-1) {
		return false;
	}
	if (seats[pos-1] != null) {
		return false;
	}
	Type x = null;
	if (advanceSale) {
		x = Configuration.Type.ADVANCE_SALE;
	}
	if (advanceSale == false) {
		x = Configuration.Type.NORMAL;
	}
	seats[pos-1] = new Seat(this, pos, x, p);
	return true;
}


@Override
public int getNumberOfAttendingChildren() {
	int children = 0;
	for (int i = 0; i<nSeats; i++) {
		if(seats[i]!=null) {
			Person x = seats[i].getHolder(); 
			if (x.getAge() < Configuration.CHILDREN_EXMAX_AGE) {
				children++;
			}
		}
	}
	return children;
}


@Override
public int getNumberOfAttendingAdults() {
	int adults = 0;
	for (int i = 0; i<nSeats; i++) {
		if(seats[i]!=null) {
			Person x = seats[i].getHolder(); 
			if (Configuration.CHILDREN_EXMAX_AGE <= x.getAge() && x.getAge() < Configuration.ELDERLY_PERSON_INMIN_AGE ) {
				adults++;
			}
		}
	}
	return adults;
}


@Override
public int getNumberOfAttendingElderlyPeople() {
	int ederly = 0;
	for (int i = 0; i<nSeats; i++) {
		if(seats[i]!=null) {
			Person x = seats[i].getHolder(); 
			if (x.getAge() >= Configuration.ELDERLY_PERSON_INMIN_AGE ) {
				ederly++;
			}
		}
	}
	return ederly;
}


@Override
public List<Integer> getAvailableSeatsList() {
	List<Integer> lista = new ArrayList<Integer>();
	for (int i = 0; i<nSeats; i++) {
		if (seats[i] == null) {
			lista.add(i+1);
		}
	}
	return lista;
}


@Override
public List<Integer> getAdvanceSaleSeatsList() {
	List<Integer> lista = new ArrayList<Integer>();
	for (int i = 0; i<nSeats; i++) {
		if (seats[i] != null) {
			if(seats[i].getType() == Configuration.Type.ADVANCE_SALE) {
				lista.add(i+1);
			}
		}
	}
	return lista;
}


@Override
public int getMaxNumberConsecutiveSeats() {
	int x=0;
	int y=0;
	for (int i = 0; i<nSeats; i++) {
		if (seats[i] == null) {
			x++;
		}
		if (seats[i] != null) {
			if (x>y) {
				y=x;
			}
			x=0;
		}
	}
	if (x>y) {
		y=x;
	}
	return y;
}


@Override
public Double getPrice(Seat seat) {
	double x = 0;
	if (seat.getEvent() != this) {
		return x;
	}
	if (seat.getType() == Configuration.Type.NORMAL) {
		x = price;
	}
	if (seat.getType() == Configuration.Type.ADVANCE_SALE) {
		x = price*((price-discountAdvanceSale)/100);
	}
	return x;
}


@Override
public Double getCollectionEvent() {
	double x = 0;
	for (int i = 0; i<nSeats; i++) {
		if(seats[i] != null) {
			x = x + getPrice(seats[i]);
		}
	}
	return x;
}


@Override
public int getPosPerson(Person p) {
	int x=-2;
	for (int i = 0; i<nSeats; i++) {
		if (seats[i]!=null) {
			if(seats[i].getHolder() == p) {
				x=i;
				break;
			}
		}
	}
	return x+1;
}


@Override
public boolean isAdvanceSale(Person p) {
		if(seats[getPosPerson(p)-1].getType() == Configuration.Type.ADVANCE_SALE) {
			return true;
	}
	return false;
}
   


}	