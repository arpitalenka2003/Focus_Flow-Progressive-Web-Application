package com.foucusflow.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.foucusflow.entity.Assignments;

public class AssignmentSpecification implements Specification<Assignments> {

	private static final long serialVersionUID = -8237756118488448098L;
	private String branch;
	private String semester;
	private String course;
	private String title;

	public AssignmentSpecification(String branch, String semester, String course) {
		super();
		this.branch = branch;
		this.semester = semester;
		this.course = course;
	}
	
	public AssignmentSpecification(String branch, String semester) {
		super();
		this.branch = branch;
		this.semester = semester;
	}
	
	public AssignmentSpecification(String title) {
		super();
		this.title = title;
	}

	@Override
	public Predicate toPredicate(Root<Assignments> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
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
		
		if (!StringUtils.isEmpty(title)) {
			predicates.add(criteriaBuilder.equal(root.get("title"), title));
		}

		return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
	}

}
