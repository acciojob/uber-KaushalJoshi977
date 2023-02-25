package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CabRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	CabRepository cabRepository;

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
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		int driverId = 0;
		for (Driver driver: driverRepository2.findAll() ) {
			Cab cab = driver.getCab();
			if(cab.isAvailable()) {
				driverId = driver.getDriverId();
				break;
			}

		}
		if(driverId ==0) throw new Exception("No cab available!");

		TripBooking tripBooking = new TripBooking();
		tripBooking.setCustomer(customerRepository2.findById(customerId).get());
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDriver(driverRepository2.findById(driverId).get());
		tripBooking.setStatus(TripStatus.CONFIRMED);
		customerRepository2.findById(customerId).get().getTripBookingList().add(tripBooking);
		driverRepository2.findById(driverId).get().getTripBookingList().add(tripBooking);
		tripBookingRepository2.save(tripBooking);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
	}
}
