package edu.rit.CSCI652.demo;

public class Subscriber {

	String ip;
	String userName;


	public Subscriber(String ip, String userName) {
		this.ip = ip;
		this.userName = userName;
	}

	public String getIp() {
		return ip;
	}

	public String getUserName() {
		return userName;
	}
}
