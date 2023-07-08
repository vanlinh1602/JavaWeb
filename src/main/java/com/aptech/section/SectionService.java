package com.aptech.section;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aptech.common.entity.section.Section;

@Service
public class SectionService {
	@Autowired private SectionRepository repo;
	
	public List<Section> listEnabledSections() {
		return repo.findAllByEnabledOrderBySectionOrderAsc(true);
	}
}
