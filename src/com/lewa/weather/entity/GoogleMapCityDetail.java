package com.lewa.weather.entity;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapCityDetail {
	String name;
	Status status;
	List<Placemark> list = new ArrayList<GoogleMapCityDetail.Placemark>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Placemark> getList() {
		return list;
	}

	public void setList(List<Placemark> list) {
		this.list = list;
	}

	public class Status {
		int code;
		String request;
	}

	public class Placemark {
		String id;
		String address;
		AddressDetails addressDetails;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public AddressDetails getAddressDetails() {
			return addressDetails;
		}

		public void setAddressDetails(AddressDetails addressDetails) {
			this.addressDetails = addressDetails;
		}
	}

	public class AddressDetails {
		int accuracy;
		Country country;

		public int getAccuracy() {
			return accuracy;
		}

		public void setAccuracy(int accuracy) {
			this.accuracy = accuracy;
		}

		public Country getCountry() {
			return country;
		}

		public void setCountry(Country country) {
			this.country = country;
		}

	}

	public class Country {
		AdministrativeArea area;
		String countryName;
		String countryNameCode;

		public AdministrativeArea getArea() {
			return area;
		}

		public void setArea(AdministrativeArea area) {
			this.area = area;
		}

		public String getCountryName() {
			return countryName;
		}

		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}

		public String getCountryNameCode() {
			return countryNameCode;
		}

		public void setCountryNameCode(String countryNameCode) {
			this.countryNameCode = countryNameCode;
		}
	}

	public class AdministrativeArea {
		String name;
		Locality locality;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Locality getLocality() {
			return locality;
		}

		public void setLocality(Locality locality) {
			this.locality = locality;
		}
	}

	public class Locality {
		DependentLocality locality;
		String localityName;

		public DependentLocality getLocality() {
			return locality;
		}

		public void setLocality(DependentLocality locality) {
			this.locality = locality;
		}

		public String getLocalityName() {
			return localityName;
		}

		public void setLocalityName(String localityName) {
			this.localityName = localityName;
		}
	}

	public class DependentLocality {
		String[] addressLine;
		String dependentLocalityName;
		Thoroughfare thoroughfare;
	}

	public class Thoroughfare {
		String thoroughfareName;
	}
}
