package com.foucusflow.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.foucusflow.dto.AssignmentRequestDto;
import com.foucusflow.dto.DocumentFilterDto;
import com.foucusflow.dto.EvaluateAnswerDto;
import com.foucusflow.dto.QuestionAnswerDto;
import com.foucusflow.dto.UpdateAssignRequestDto;
import com.foucusflow.dto.UserDto;
import com.foucusflow.entity.Answers;
import com.foucusflow.entity.Assignments;

public interface IAssignmentService {

	void saveAssignment(AssignmentRequestDto reqDto);

	Page<Assignments> fetchAssignments(Integer page);

	Page<Assignments> fetchFilteredAssignments(Integer page, DocumentFilterDto docFilterDto);

	void editAssignment(UpdateAssignRequestDto assignDto);

	void deleteAssignment(long id);

	List<Map<String, String>> fetchAssignmentTitlesByStudent(UserDto user);

	List<Assignments> fetchQuestionsForStudentsByTitle(String title);

	void updateAssignmentStatusForUser(long id, String title) throws Exception;

	void submitAnswer(UserDto user, List<QuestionAnswerDto> questionAnswers, String title);

	List<Map<String, String>> getCompletedTitleByStudentId(long id);

	List<Answers> getAnswersForTitle(long id);

	void updateSecuredMark(List<EvaluateAnswerDto> list);

	boolean getAssignmentByTitle(String title);
}