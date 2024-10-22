package ua.foxminded.carrestservice.service;

import org.springframework.stereotype.Service;

import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenerateCarIdService {

	public String generateUniquetId() {
		String tsid = TSID.Factory.getTsid().toString();
		return tsid.replaceAll("[^A-Z0-9]", "").substring(0, 10).toUpperCase();
	}
}
