package com.foucusflow.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.foucusflow.entity.Document;

public class DocumentSpecification implements Specification<Document> {
	private static final long serialVersionUID = -6727299587344695452L;
	
	private String branch;
	private String semester;
	private String course;

	public DocumentSpecification(String branch, String semester, String course) {
		super();
		this.branch = branch;
		this.semester = semester;
		this.course = course;
	}

	@Override
	public Predicate toPredicate(Root<Document> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		List<Predicate> predicates = new ArrayList<>();

		if (!StringUtils.isEmpty(branch)) {
			predicates.add(criteriaBuilder.equal(root.get("branch"), branch));
		}

		if (!StringUtils.isEmpty(semester)) {
			predicates.add(criteriaBuilder.equal(root.get("semester"), semester));
		}

		if (!StringUtils.isEmpty(course)) {
			predicates.add(criteriaBuilder.equal(root.get("course"), course));
		}

		return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
	}

}
