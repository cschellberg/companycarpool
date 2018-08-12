package com.eligaapps.companycarpool.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class DOSDetector {

	private int maximumHits = 10;

	private ConcurrentHashMap<String, AtomicInteger> ipMap = new ConcurrentHashMap<>();

	public boolean hasExceededLimitsIncr(final String ipAddress) {
		AtomicInteger hits = ipMap.computeIfAbsent(ipAddress, k -> new AtomicInteger(0));
		return (hits.incrementAndGet() > maximumHits);
	}

	public boolean hasExceededLimits(final String ipAddress) {
		AtomicInteger hits = ipMap.computeIfAbsent(ipAddress, k -> new AtomicInteger(0));
		return (hits.get() > maximumHits);
	}
	
	public void clearIP(final String ipAddress){
		ipMap.remove(ipAddress);
	}

}
