package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CabRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

//	@Autowired
//	CabRepository cabRepository;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
//		Customer customer1 = new Customer();
//		customer1.setMobile(customer.getMobile());
//		customer1.setPassword(customer.getPassword());
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function0
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception {
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		Driver driver1 = null;
		for (Driver driver : driverRepository2.findAll()) {
			Cab cab = driver.getCab();
			if (cab.getAvailable()) {
				driver1 = driver;
				break;
			}

		}
		if (driver1 == null) return null;
			//throw new Exception("No cab available!");

		Customer customer = customerRepository2.findById(customerId).get();
		Cab cab = driver1.getCab();

		TripBooking tripBooking = new TripBooking();
		tripBooking.setCustomer(customer);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDriver(driver1);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setFromLocation(fromLocation);
		cab.setAvailable(false);
		driver1.setCab(cab);

		List<TripBooking> tripBookingList = driver1.getTripBookingList();
		tripBookingList.add(tripBooking);
		driver1.setTripBookingList(tripBookingList);

		List<TripBooking> tripBookingList1 = customer.getTripBookingList();
		tripBookingList1.add(tripBooking);
		customer.setTripBookingList(tripBookingList1);

		customerRepository2.save(customer);
		driverRepository2.save(driver1);
		tripBookingRepository2.save(tripBooking);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		Driver driver = tripBooking.getDriver();
		Cab cab = driver.getCab();
		cab.setAvailable(false);
		driver.setCab(cab);
		driverRepository2.save(driver);
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		Driver driver = tripBooking.getDriver();
		Cab cab = driver.getCab();
		cab.setAvailable(true);
		driver.setCab(cab);
		driverRepository2.save(driver);
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);
	}
}
