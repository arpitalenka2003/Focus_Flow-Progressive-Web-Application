package com.foucusflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.foucusflow.dto.AssignmentDto;
import com.foucusflow.dto.AssignmentRequestDto;
import com.foucusflow.dto.DocumentFilterDto;
import com.foucusflow.dto.EvaluateAnswerDto;
import com.foucusflow.dto.QuestionAnswerDto;
import com.foucusflow.dto.UpdateAssignRequestDto;
import com.foucusflow.dto.UserDto;
import com.foucusflow.entity.Answers;
import com.foucusflow.entity.Assignments;
import com.foucusflow.entity.StudentAssignments;
import com.foucusflow.repo.AnswersRepository;
import com.foucusflow.repo.AssignmentRepository;
import com.foucusflow.repo.StudentAssignmentsRepository;
import com.foucusflow.service.IAssignmentService;
import com.foucusflow.specification.AssignmentSpecification;

@Service
public class AssignmentServiceImpl implements IAssignmentService {

	@Autowired
	private AssignmentRepository assignmentRepo;

	@Autowired
	private StudentAssignmentsRepository stuAssignmentRepo;

	@Autowired
	private AnswersRepository answerRepo;

	@Override
	public Page<Assignments> fetchAssignments(Integer page) {
		return assignmentRepo.findAll(PageRequest.of(page, 10));
	}

	@Override
	public Page<Assignments> fetchFilteredAssignments(Integer page, DocumentFilterDto docFilterDto) {
		AssignmentSpecification specification = new AssignmentSpecification(docFilterDto.getBranch(),
				docFilterDto.getSemester(), docFilterDto.getCourse());
		return assignmentRepo.findAll(specification, PageRequest.of(page, 10));
	}

	@Override
	public void saveAssignment(AssignmentRequestDto reqDto) {
		List<Assignments> assignments = new ArrayList<>();
		for (AssignmentDto assignmentDto : reqDto.getAssignments()) {
			Assignments assignment = new Assignments();
			assignment.setBranch(reqDto.getBranch());
			assignment.setSemester(reqDto.getSemester());
			assignment.setCourse(reqDto.getCourse());
			assignment.setDuration(reqDto.getDuration());
			assignment.setTitle(reqDto.getTitle());
			assignment.setQuestion(assignmentDto.getQuestion());
			assignment.setMark(assignmentDto.getMark());
			assignment.setMcq1(assignmentDto.getMcq1());
			assignment.setMcq2(assignmentDto.getMcq2());
			assignment.setMcq3(assignmentDto.getMcq3());
			assignment.setMcq4(assignmentDto.getMcq4());

			assignments.add(assignment);
		}

		assignmentRepo.saveAll(assignments);
	}

	@Override
	public void editAssignment(UpdateAssignRequestDto assignDto) {
		Assignments assignment = assignmentRepo.findById(assignDto.getId()).orElse(null);

		if (assignment != null) {
			assignment.setTitle(assignDto.getTitle());
			assignment.setDuration(assignDto.getDuration());
			assignment.setMark(assignDto.getMark());
			assignment.setQuestion(assignDto.getQuestion());
			assignment.setMcq1(assignDto.getMcq1());
			assignment.setMcq2(assignDto.getMcq2());
			assignment.setMcq3(assignDto.getMcq3());
			assignment.setMcq4(assignDto.getMcq4());

			assignmentRepo.save(assignment);
		}
	}

	@Override
	public void deleteAssignment(long id) {
		assignmentRepo.deleteById(id);
	}

	@Override
	public List<Map<String, String>> fetchAssignmentTitlesByStudent(UserDto user) {
		AssignmentSpecification specification = new AssignmentSpecification(user.getBranch(), user.getSemester());
		List<Assignments> assignmentsList = assignmentRepo.findAll(specification);

		Set<String> uniqueTitles = assignmentsList.stream().map(Assignments::getTitle).collect(Collectors.toSet());

		List<Map<String, String>> list = new ArrayList<>();

		for (String title : uniqueTitles) {
			Map<String, String> map = new HashMap<>();
			int totalMark = 0;
			int mcq = 0;
			int noneMcq = 0;
			int markSecured = 0;

			for (Assignments assignments : assignmentsList) {
				if (assignments.getTitle().equals(title)) {
					totalMark += Integer.valueOf(assignments.getMark());
					map.put("duration", assignments.getDuration());

					if (!StringUtils.isEmpty(assignments.getMcq1())) {
						mcq += 1;
					} else {
						noneMcq += 1;
					}
				}
			}
			map.put("title", title);
			map.put("totalMark", String.valueOf(totalMark));
			map.put("mcq", String.valueOf(mcq));
			map.put("noneMcq", String.valueOf(noneMcq));
			map.put("markSecured", "");

			StudentAssignments studentAssignments = stuAssignmentRepo.findByStudentIdAndTitle(user.getId(), title);
			if (studentAssignments != null) {
				map.put("completionStatus", "Completed");

				List<Answers> answers = studentAssignments.getAnswers();

				if (answers != null) {
					for (Answers answer : answers) {
						if (answer.getMarkSecured() != null) {
							markSecured += Integer.valueOf(answer.getMarkSecured());

							map.put("markSecured", String.valueOf(markSecured));
						}
					}
				}
			} else {
				map.put("completionStatus", "Incomplete");
			}
			list.add(map);
		}

		return list;
	}

	@Override
	public List<Assignments> fetchQuestionsForStudentsByTitle(String title) {
		AssignmentSpecification specification = new AssignmentSpecification(title);
		return assignmentRepo.findAll(specification);
	}

	@Override
	public void updateAssignmentStatusForUser(long id, String title) throws Exception {
		StudentAssignments studentAssignments = stuAssignmentRepo.findByStudentIdAndTitle(id, title);

		if (studentAssignments == null) {
			studentAssignments = new StudentAssignments();
			studentAssignments.setTitle(title);
			studentAssignments.setStudentId(id);
			studentAssignments.setStartDate(new Date());
			stuAssignmentRepo.save(studentAssignments);
		} else {
			throw new Exception("Answer allready submitted");
		}
	}

	@Override
	public void submitAnswer(UserDto user, List<QuestionAnswerDto> questionAnswers, String title) {
		StudentAssignments studentAssignments = stuAssignmentRepo.findByStudentIdAndTitle(user.getId(), title);

		if (studentAssignments != null && studentAssignments.getEndDate() == null) {
			List<Answers> answers = new ArrayList<>();
			for (QuestionAnswerDto dto : questionAnswers) {
				Answers answer = new Answers();
				answer.setQuestion(dto.getQuestion());
				answer.setAnswer(dto.getAnswer());
				answer.setStuAssignments(studentAssignments);
				answer.setMark(dto.getMark());
				answers.add(answer);
			}
			studentAssignments.setEndDate(new Date());
			studentAssignments.setAnswers(answers);

			stuAssignmentRepo.save(studentAssignments);
		}
	}

	@Override
	public List<Map<String, String>> getCompletedTitleByStudentId(long id) {
		List<StudentAssignments> studentAssignments = stuAssignmentRepo.findAllByStudentId(id);

		List<Map<String, String>> list = new ArrayList<>();

		for (StudentAssignments studentAssignment : studentAssignments) {
			if (studentAssignment.getAnswers().get(0).getMarkSecured() == null
					|| studentAssignment.getAnswers().get(0).getMarkSecured().isEmpty()) {
				Map<String, String> map = new HashMap<>();
				map.put("id", String.valueOf(studentAssignment.getId()));
				map.put("title", String.valueOf(studentAssignment.getTitle()));

				list.add(map);
			}
		}

		return list;
	}

	@Override
	public List<Answers> getAnswersForTitle(long id) {
		return answerRepo.findAllByStuAssignmentId(id);
	}

	@Override
	public void updateSecuredMark(List<EvaluateAnswerDto> list) {
		for (EvaluateAnswerDto dto : list) {
			Answers answer = answerRepo.findById(Long.parseLong(dto.getId())).orElse(null);

			if (answer != null) {
				answer.setMarkSecured(dto.getMark());
				answerRepo.save(answer);
			}
		}

	}

	@Override
	public boolean getAssignmentByTitle(String title) {
		return assignmentRepo.findAllByTitle(title).isEmpty();
	}
}