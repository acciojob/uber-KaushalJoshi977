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
			if(cab.getAvailable()) {
				driverId = driver.getDriverId();
				break;
			}

		}
		if(driverId ==0) throw new Exception("No cab available!");

		Customer customer = customerRepository2.findById(customerId).get();
		Driver driver = driverRepository2.findById(driverId).get();
		Cab cab = driver.getCab();

		TripBooking tripBooking = new TripBooking();
		tripBooking.setCustomer(customer);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDriver(driver);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		cab.setAvailable(false);
		driver.setCab(cab);

		List<TripBooking> tripBookingList = driver.getTripBookingList();
		tripBookingList.add(tripBooking);
		driver.setTripBookingList(tripBookingList);

		List<TripBooking> tripBookingList1 = customer.getTripBookingList();
		tripBookingList1.add(tripBooking);
		customer.setTripBookingList(tripBookingList1);

		customerRepository2.save(customer);
		driverRepository2.save(driver);
		tripBookingRepository2.save(tripBooking);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		Cab cab = tripBooking.getDriver().getCab();
		cab.setAvailable(true);
		cabRepository.save(cab);
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		Cab cab = tripBooking.getDriver().getCab();
		cab.setAvailable(true);
		cabRepository.save(cab);
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);
	}
}
