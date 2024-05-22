package com.foucusflow.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foucusflow.dto.AssignmentRequestDto;
import com.foucusflow.dto.CreateTodoDto;
import com.foucusflow.dto.DocumentFilterDto;
import com.foucusflow.dto.DocumentUploadDto;
import com.foucusflow.dto.EvaluateAnswerDto;
import com.foucusflow.dto.NoteReqDto;
import com.foucusflow.dto.Result;
import com.foucusflow.dto.UpdateAssignRequestDto;
import com.foucusflow.dto.UserDto;
import com.foucusflow.dto.UserEditDto;
import com.foucusflow.entity.Answers;
import com.foucusflow.entity.Assignments;
import com.foucusflow.entity.Contacts;
import com.foucusflow.entity.Document;
import com.foucusflow.entity.Message;
import com.foucusflow.entity.Note;
import com.foucusflow.entity.Todo;
import com.foucusflow.entity.User;
import com.foucusflow.service.IAssignmentService;
import com.foucusflow.service.IContactsService;
import com.foucusflow.service.IDocumentService;
import com.foucusflow.service.IMessageService;
import com.foucusflow.service.INoteService;
import com.foucusflow.service.ITodoService;
import com.foucusflow.service.IUserService;
import com.foucusflow.service.impl.AssignmentServiceImpl;
import com.foucusflow.service.impl.ContactsServiceImpl;
import com.foucusflow.service.impl.DocumentServiceImpl;
import com.foucusflow.service.impl.MessageServiceImpl;
import com.foucusflow.service.impl.NoteServiceImpl;
import com.foucusflow.service.impl.TodoServiceImpl;
import com.foucusflow.service.impl.UserServiceImpl;
import com.foucusflow.util.AppConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

	private final IUserService userService;

	private final IContactsService contactService;

	private final IMessageService messageService;

	private final IDocumentService docService;

	private final ITodoService todoService;

	private final IAssignmentService assignmentService;

	private final INoteService noteService;

	public static final String DEL_DOC_MSG = "delete_doc_msg";
	
	public static final String EVALUATE_PAGE_URL = "faculty/evaluate";
	
	public static final String FAC_TODO_PAGE_URL = "redirect:/faculty/todo-list/view/";

	@Autowired
	public FacultyController(UserServiceImpl userService, ContactsServiceImpl contactService,
			MessageServiceImpl messageService, DocumentServiceImpl docService, TodoServiceImpl todoService,
			AssignmentServiceImpl assignmentService, NoteServiceImpl noteService) {
		this.userService = userService;
		this.contactService = contactService;
		this.messageService = messageService;
		this.docService = docService;
		this.todoService = todoService;
		this.assignmentService = assignmentService;
		this.noteService = noteService;
	}

	@RequestMapping("/dashboard")
	public String studentDashboard(Model model, HttpSession session, Principal principal) {
		model.addAttribute(AppConstant.TITLE, "Faculty - Dashboard");

		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);

		session.setAttribute(AppConstant.USER, user);
		session.setAttribute(AppConstant.USER_DTO, new UserDto(user));
		session.setAttribute(AppConstant.USER_NAME, userName);
		model.addAttribute(AppConstant.USER_NAME, userName);
		return "faculty/faculty_dashboard";
	}

	@PostMapping("/profile/edit")
	public String editFacultyInfo(@ModelAttribute("docDto") UserEditDto editDto, HttpSession session) {
		try {
			UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);

			if (user != null) {
				userService.editUserInfo(editDto);
				session.setAttribute("user_msg", new Result("User profile updated", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute("user_msg", new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}

		return "redirect:/faculty/dashboard";
	}

	@RequestMapping("/dashboard/chat/{name}")
	public String chat(@PathVariable("name") String name, Model model, Principal principal) {
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		model.addAttribute(AppConstant.USER_NAME, userName);
		model.addAttribute("user", user);
		return "faculty/chat-app";
	}

	@GetMapping("/getAllContacts")
	public ResponseEntity<?> getAllFriends(String requestData, HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			List<Contacts> friends = contactService.getAllContacts(Long.parseLong(requestData));
			return ResponseEntity.ok().body(friends);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}

	}

	@GetMapping("/getAllChatUsers")
	public ResponseEntity<?> getAllChatUsers(HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			return ResponseEntity.ok().body(userService.getAllChatUser("faculty"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@GetMapping("/get-last-message")
	public ResponseEntity<?> getLastMessage(String requestData, String myId, HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);

			TypeToken<List<User>> token = new TypeToken<List<User>>() {
			};
			List<User> contacts = new Gson().fromJson(requestData, token.getType());
			Map<Integer, List<?>> map = new HashMap<>();
			int i = 1;
			if (!contacts.isEmpty()) {
				for (User user : contacts) {
					List<Message> messages = new ArrayList<>();
					if (messageService.getFriendLastMessages(Long.parseLong(myId), user.getId()) != null
							&& messageService.getFriendLastMessages(user.getId(), Long.parseLong(myId)) != null) {
						messages.addAll(messageService.getFriendLastMessages(Long.parseLong(myId), user.getId()));
						messages.addAll(messageService.getFriendLastMessages(user.getId(), Long.parseLong(myId)));
						map.put(i, messages);
						i++;

					} else {
						if (messageService.getFriendLastMessages(Long.parseLong(myId), user.getId()) != null) {
							messages.addAll(messageService.getFriendLastMessages(Long.parseLong(myId), user.getId()));
							map.put(i, messages);
							i++;
						}
						if (messageService.getFriendLastMessages(user.getId(), Long.parseLong(myId)) != null) {
							messages.addAll(messageService.getFriendLastMessages(user.getId(), Long.parseLong(myId)));
							map.put(i, messages);
							i++;

						}
					}
				}
			}
			return ResponseEntity.ok().body(map);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@GetMapping("/getAllBlockFriends")
	public ResponseEntity<?> listOfBlockFriends(String requestData, HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			return ResponseEntity.ok().body(contactService.listOfBlockedContacts(Long.parseLong(requestData)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@GetMapping("/createContact")
	public ResponseEntity<?> makeFriends(String requestData, String givenName, HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			Contacts f = contactService.checkBlockedOrNot(c.getId(), Long.parseLong(requestData));
			if (f == null) {
				User contact = userService.getSingleUser(Long.parseLong(requestData));
				return ResponseEntity.ok().body(contactService.saveMyContact(givenName, contact, c.getId()));
			} else {
				return ResponseEntity.ok().body(contactService.saveMyContact1(f.getId(), givenName, f.getMyContacts(),
						c.getId(), f.isBlocked()));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}

	}

	@GetMapping("/get-message")
	public ResponseEntity<?> fetchMessage(String requestData, HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			JSONObject json = new JSONObject(requestData);
			long userId = json.getLong("u_id");
			long conId = json.getLong("f_id");
			List<Message> list = new ArrayList<>();
			list.addAll(messageService.getContactMessages(userId, conId, c.getId()));
			list.addAll(messageService.getContactMessages(conId, userId, c.getId()));
			return ResponseEntity.ok().body(list);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@PostMapping("/send-message")
	public ResponseEntity<?> saveUserMessage(String requestData, HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			JSONObject json = new JSONObject(requestData);
			long userId = json.getLong("username");
			long fId = json.getLong("friend_id");
			String message = json.getString("myMessage");
			User f = userService.getSingleUser(fId);
			Set<Message> set = new HashSet<>();
			set.add(messageService.saveMessage(userId,
					new Message(f, message.getBytes(), "", LocalDateTime.now().toString())));
			return ResponseEntity.ok().body(set);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@PostMapping("/recieved-message")
	public ResponseEntity<?> saveRecievedMessage(String requestData, HttpSession session) {
		try {
			User c = (User) session.getAttribute("user");
			if (c == null)
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			JSONObject json = new JSONObject(requestData);
			long recievedMessageId = json.getLong("recievedMessageId");
			Message mId = messageService.getSingleMessage(recievedMessageId);
			Set<Message> set = new HashSet<>();

			set.add(messageService.updateMessage(new Message(mId.getId(), mId.getToUser(), mId.getContent(), null,
					mId.getMsgLabel(), mId.getSendDate(), LocalDateTime.now().toString())));
			return ResponseEntity.ok().body(set);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@RequestMapping("/doc/upload-page")
	public String uploadPage(HttpSession session) {
		return "faculty/upload_doc";
	}

	@RequestMapping("/doc/upload-doc")
	public String uploadDocument(@ModelAttribute("docDto") DocumentUploadDto docDto, HttpSession session) {

		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				docService.uploadDocument(docDto);
			}

			session.setAttribute("upload_doc_msg", new Result("Document Uploaded", AppConstant.SUCCESS_STATUS));
		} catch (Exception e) {
			session.setAttribute("upload_doc_msg", new Result("Unable to upload document", AppConstant.DANGER_STATUS));
		}

		return "faculty/upload_doc";
	}

	@RequestMapping("/doc/view/{page}")
	public String viewDocPage(@PathVariable("page") Integer page, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);
		if (!StringUtils.isEmpty(userName)) {
			Page<Document> docs = docService.fetchDocuments(page);

			model.addAttribute("docs", docs);
			model.addAttribute(AppConstant.CUR_PAGE, page);
			model.addAttribute(AppConstant.TOTAL_PAGES, docs.getTotalPages());
		}
		return "faculty/view_doc";
	}

	@RequestMapping("/doc/delete/{page}/{id}")
	public String deleteDocument(@PathVariable("page") Integer page, @PathVariable("id") Integer id, Model model,
			HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);
			if (!StringUtils.isEmpty(userName)) {
				Document doc = docService.fetchById(id);

				if (doc != null) {
					docService.deleteDocById(id);
					session.setAttribute(DEL_DOC_MSG, new Result("Document Deleted", AppConstant.SUCCESS_STATUS));
				} else {
					session.setAttribute(DEL_DOC_MSG, new Result("Document doesn't exist", AppConstant.DANGER_STATUS));
				}
			}
		} catch (Exception e) {
			session.setAttribute(DEL_DOC_MSG, new Result("Unable to delete document", AppConstant.DANGER_STATUS));
		}
		return viewDocPage(page, model, session);
	}

	@RequestMapping("/doc/download/{id}")
	public void downloadDocument(@PathVariable("id") Integer id, Model model, HttpSession session,
			HttpServletResponse response) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);
			if (!StringUtils.isEmpty(userName)) {
				Document doc = docService.fetchById(id);
				if (doc != null) {
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename = " + doc.getFileName());

					ServletOutputStream outputStream = response.getOutputStream();
					outputStream.write(doc.getDocument());
					outputStream.close();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	@RequestMapping(value = "/doc/open/{id}")
	public String createNotePage(@PathVariable("id") Integer id, Model model, HttpSession session) {

		session.setAttribute(AppConstant.DOC_ID, id);
		model.addAttribute("noteDto", new NoteReqDto());

		return "faculty/create_note";
	}

	@RequestMapping(value = "/doc/open")
	public ResponseEntity<byte[]> viewDocument(Model model, HttpSession session) throws IOException {

		int id = (int) session.getAttribute(AppConstant.DOC_ID);
		Document doc = docService.fetchById(id);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(Files.probeContentType(new File(doc.getFileName()).toPath())));
		headers.add("content-disposition", "inline;filename=" + doc.getFileName());
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

		session.removeAttribute(AppConstant.DOC_ID);
		model.addAttribute("filename", doc.getFileName());

		return new ResponseEntity<>(doc.getDocument(), headers, HttpStatus.OK);
	}

	@RequestMapping("/doc/filter-view/{page}")
	public String viewDocPage(@ModelAttribute("docFilterDto") DocumentFilterDto docFilterDto,
			@PathVariable("page") Integer page, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);
		if (!StringUtils.isEmpty(userName)) {
			Page<Document> docs = docService.fetchFilteredDocuments(page, docFilterDto);

			model.addAttribute("docs", docs);
			model.addAttribute(AppConstant.CUR_PAGE, page);
			model.addAttribute(AppConstant.TOTAL_PAGES, docs.getTotalPages());
		}
		return "faculty/view_doc";
	}

	@RequestMapping("/todo-list/view/{page}")
	public String viewTodoList(@PathVariable("page") Integer page, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		if (!StringUtils.isEmpty(userName)) {
			User user = (User) session.getAttribute(AppConstant.USER);
			Page<Todo> todos = todoService.fetchTodosByUser(page, user.getId());
			model.addAttribute("todos", todos);
			model.addAttribute(AppConstant.CUR_PAGE, page);
			model.addAttribute(AppConstant.TOTAL_PAGES, todos.getTotalPages());
		}
		return "faculty/todo_list";
	}

	@RequestMapping("/todo-list/create")
	public String createTodo(@ModelAttribute("todoDto") CreateTodoDto todoDto, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				User user = (User) session.getAttribute(AppConstant.USER);
				todoService.createTodo(user.getId(), todoDto);
				session.setAttribute(AppConstant.TODO_MSG, new Result("Todo created", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.TODO_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "redirect:/faculty/todo-list/view/0";
	}

	@RequestMapping("/todo-list/complete/{page}/{id}")
	public String markTodoComplete(@PathVariable("page") Integer page, @PathVariable("id") long id,
			HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				todoService.markTodoComplete(id);
				session.setAttribute(AppConstant.TODO_MSG, new Result("Todo completed", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.TODO_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return FAC_TODO_PAGE_URL + page;
	}

	@RequestMapping("/todo-list/incomplete/{page}/{id}")
	public String markTodoInComplete(@PathVariable("page") Integer page, @PathVariable("id") long id,
			HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				todoService.markTodoInComplete(id);
				session.setAttribute(AppConstant.TODO_MSG, new Result("Todo incomplete", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.TODO_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return FAC_TODO_PAGE_URL + page;
	}

	@RequestMapping("/todo-list/edit/{page}")
	public String editTodo(@PathVariable("page") Integer page, @ModelAttribute("todoDto") CreateTodoDto todoDto,
			HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				todoService.editTodo(todoDto);
				session.setAttribute(AppConstant.TODO_MSG, new Result("Todo updated", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.TODO_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return FAC_TODO_PAGE_URL + page;
	}

	@RequestMapping("/todo-list/delete/{page}/{id}")
	public String deleteTodo(@PathVariable("page") Integer page, @PathVariable("id") long id, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				todoService.deleteTodo(id);
				session.setAttribute(AppConstant.TODO_MSG, new Result("Todo deleted", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.TODO_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return FAC_TODO_PAGE_URL + page;
	}

	@RequestMapping("/assignment/filter-view/{page}")
	public String viewFilteredAssignments(@ModelAttribute("docFilterDto") DocumentFilterDto docFilterDto,
			@PathVariable("page") Integer page, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);
		if (!StringUtils.isEmpty(userName)) {
			Page<Assignments> assignments = assignmentService.fetchFilteredAssignments(page, docFilterDto);

			model.addAttribute("assignments", assignments);
			model.addAttribute(AppConstant.CUR_PAGE, page);
			model.addAttribute(AppConstant.TOTAL_PAGES, assignments.getTotalPages());
		}
		return "faculty/assignments";
	}

	@RequestMapping("/assignment/view/{page}")
	public String viewAssignments(@PathVariable("page") Integer page, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		if (!StringUtils.isEmpty(userName)) {
			Page<Assignments> assignments = assignmentService.fetchAssignments(page);
			model.addAttribute("assignments", assignments);
			model.addAttribute(AppConstant.CUR_PAGE, page);
			model.addAttribute(AppConstant.TOTAL_PAGES, assignments.getTotalPages());
		}
		return "faculty/assignments";
	}

	@RequestMapping("/assignment/create")
	public ResponseEntity<String> createAssignments(@RequestBody AssignmentRequestDto reqDto, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				if (!assignmentService.getAssignmentByTitle(reqDto.getTitle())) {
					session.setAttribute(AppConstant.ASSIGN_MSG,
							new Result("Asssignment all ready exist with this title", AppConstant.DANGER_STATUS));
					return new ResponseEntity<>("Asssignment all ready exist with this title", HttpStatus.BAD_REQUEST);
				}
				assignmentService.saveAssignment(reqDto);
				session.setAttribute(AppConstant.ASSIGN_MSG, new Result("Assessment created", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.ASSIGN_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return new ResponseEntity<>("Asssignment Created", HttpStatus.OK);
	}

	@RequestMapping("/assignment/edit/{page}")
	public String editAssignment(@PathVariable("page") Integer page,
			@ModelAttribute("assignDto") UpdateAssignRequestDto assignDto, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				assignmentService.editAssignment(assignDto);
				session.setAttribute(AppConstant.ASSIGN_MSG, new Result("Assessment updated", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.ASSIGN_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "redirect:/faculty/assignment/view/" + page;
	}

	@RequestMapping("/assignment/delete/{page}/{id}")
	public String deleteAssignment(@PathVariable("page") Integer page, @PathVariable("id") long id,
			HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				assignmentService.deleteAssignment(id);
				session.setAttribute(AppConstant.ASSIGN_MSG, new Result("Assessment deleted", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.ASSIGN_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "redirect:/faculty/assignment/view/" + page;
	}

	@RequestMapping("/assignment/evaluate")
	public String evaluatePage(Model model, HttpSession session) {
		return EVALUATE_PAGE_URL;
	}

	@RequestMapping("/student/fetch")
	public ResponseEntity<List<Map<String, String>>> getStudentsForEvaluation(@RequestBody Map<String, String> params,
			HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		List<Map<String, String>> data = new ArrayList<>();

		if (!StringUtils.isEmpty(userName)) {
			data = userService.getStudentsForEvaluation(params);
		}

		return ResponseEntity.ok(data);
	}

	@RequestMapping("/title/fetch/{studentId}")
	public ResponseEntity<List<Map<String, String>>> getCompletedTitleByStudentId(
			@PathVariable("studentId") long studentId, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		List<Map<String, String>> data = new ArrayList<>();

		if (!StringUtils.isEmpty(userName)) {
			data = assignmentService.getCompletedTitleByStudentId(studentId);
		}

		return ResponseEntity.ok(data);
	}

	@RequestMapping("/assignment/answers")
	public String fetchAnswers(@RequestParam("titleId") long titleId, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		if (!StringUtils.isEmpty(userName)) {
			List<Answers> answers = assignmentService.getAnswersForTitle(titleId);

			model.addAttribute("answers", answers);
		}
		return EVALUATE_PAGE_URL;
	}

	@RequestMapping("/assignment/evaluate-answer")
	public String evaluateAnswer(@RequestBody List<EvaluateAnswerDto> list, Model model, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				assignmentService.updateSecuredMark(list);
				session.setAttribute("evaluate_answer", new Result("Evaluation Done", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute("evaluate_answer", new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}

		return EVALUATE_PAGE_URL;
	}

	@RequestMapping("/change_password")
	public String changePasswordPage(HttpSession session) {
		return "faculty/change_password";
	}

	@RequestMapping("/update_password")
	public String updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
			HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		if (!StringUtils.isEmpty(userName)) {

			boolean updated = userService.updatePassword(userName, oldPassword, newPassword);

			if (updated) {
				session.setAttribute("pwd_change_msg", new Result("Password Updated.", AppConstant.SUCCESS_STATUS));
				return "redirect:/logout";
			} else {
				session.setAttribute("pwd_change_msg", new Result("Password Not Matching.", "danger"));
			}
		}

		return "faculty/change_password";
	}
	
	@RequestMapping("/note/create")
	public String createNote(@ModelAttribute("noteDto") NoteReqDto noteDto, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);
				noteService.createNote(user.getId(), noteDto);
				session.setAttribute(AppConstant.NOTE_MSG, new Result("Note Created", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.NOTE_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "redirect:/faculty/note/view/0";
	}

	@RequestMapping("/note/view/{page}")
	public String viewNotes(@PathVariable("page") Integer page, Model model, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);
				Page<Note> notes = noteService.fetchNotesByUser(page, user.getId());
				model.addAttribute("notes", notes);
				model.addAttribute(AppConstant.CUR_PAGE, page);
				model.addAttribute(AppConstant.TOTAL_PAGES, notes.getTotalPages());

				model.addAttribute(AppConstant.TITLE, "Faculty - Notes");
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.NOTE_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "faculty/notes";
	}

	@RequestMapping("/note/edit/{page}")
	public String editNote(@PathVariable("page") Integer page, @ModelAttribute("noteDto") NoteReqDto noteDto,
			HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				noteService.editNote(noteDto);
				session.setAttribute(AppConstant.NOTE_MSG, new Result("Note Updated", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.NOTE_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "redirect:/faculty/note/view/" + page;
	}

	@RequestMapping("/note/delete/{page}/{id}")
	public String deleteNote(@PathVariable("page") Integer page, @PathVariable("id") long id, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				noteService.deleteNote(id);
				session.setAttribute(AppConstant.NOTE_MSG, new Result("Note Deleted", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.NOTE_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "redirect:/faculty/note/view/" + page;
	}
}