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

import com.foucusflow.dto.CreateTodoDto;
import com.foucusflow.dto.NoteReqDto;
import com.foucusflow.dto.QuestionAnswerDto;
import com.foucusflow.dto.Result;
import com.foucusflow.dto.UserDto;
import com.foucusflow.dto.UserEditDto;
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
@RequestMapping("/student")
public class StudentController {

	private final IUserService userService;

	private final IContactsService contactService;

	private final IMessageService messageService;

	private final IDocumentService docService;

	private final ITodoService todoService;

	private final IAssignmentService assignmentService;

	private final INoteService noteService;

	public static final String RED_TODOLIST_VIEW_URL = "redirect:/student/todo-list/view/";

	@Autowired
	public StudentController(UserServiceImpl userService, ContactsServiceImpl contactService,
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

	@RequestMapping("dashboard")
	public String studentDashboard(Model model, Principal principal, HttpSession session) {
		model.addAttribute(AppConstant.TITLE, "Student - Dashboard");

		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);

		session.setAttribute(AppConstant.USER, user);
		session.setAttribute(AppConstant.USER_DTO, new UserDto(user));
		session.setAttribute(AppConstant.USER_NAME, userName);
		model.addAttribute(AppConstant.USER_NAME, userName);
		return "student/student_dashboard";
	}

	@PostMapping("/profile/edit")
	public String editStudentInfo(@ModelAttribute("docDto") UserEditDto editDto, HttpSession session) {
		try {
			UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);

			if (user != null) {
				userService.editUserInfo(editDto);
				session.setAttribute("user_msg", new Result("User profile updated", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute("user_msg", new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}

		return "redirect:/student/dashboard";
	}

	@RequestMapping("/dashboard/chat/{name}")
	public String chat(@PathVariable("name") String name, Model model, HttpSession session) {
		User user = (User) session.getAttribute(AppConstant.USER);
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		model.addAttribute("userName", userName);
		model.addAttribute("user", new UserDto(user));
		model.addAttribute(AppConstant.TITLE, "Student - Chat");

		return "student/chat-app";
	}

	@GetMapping("/getAllContacts")
	public ResponseEntity<?> getAllFriends(String requestData, HttpSession session) {
		try {
			UserDto c = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			if (c == null) {
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			}
			List<Contacts> friends = contactService.getAllContacts(Long.parseLong(requestData));
			return ResponseEntity.ok().body(friends);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@GetMapping("/getAllChatUsers")
	public ResponseEntity<?> getAllChatUsers(HttpSession session) {
		try {
			UserDto c = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			if (c == null) {
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			}
			return ResponseEntity.ok().body(userService.getAllChatUser("student"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
		}
	}

	@GetMapping("/get-last-message")
	public ResponseEntity<?> getLastMessage(String requestData, String myId, HttpSession session) {
		try {
			UserDto c = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			if (c == null) {
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			}
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
		}
	}

	@GetMapping("/getAllBlockFriends")
	public ResponseEntity<?> listOfBlockFriends(String requestData, HttpSession session) {
		try {
			UserDto c = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			if (c == null) {
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			}
			return ResponseEntity.ok().body(contactService.listOfBlockedContacts(Long.parseLong(requestData)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@GetMapping("/createContact")
	public ResponseEntity<?> makeFriends(String requestData, String givenName, HttpSession session) {
		try {
			UserDto c = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			if (c == null) {
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			}
			Contacts f = contactService.checkBlockedOrNot(c.getId(), Long.parseLong(requestData));
			if (f == null) {
				User contact = userService.getSingleUser(Long.parseLong(requestData));
				return ResponseEntity.ok().body(contactService.saveMyContact(givenName, contact, c.getId()));
			} else {
				return ResponseEntity.ok().body(contactService.saveMyContact1(f.getId(), givenName, f.getMyContacts(),
						c.getId(), f.isBlocked()));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
		}

	}

	@GetMapping("/get-message")
	public ResponseEntity<?> fetchMessage(String requestData, HttpSession session) {
		try {
			UserDto c = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			if (c == null) {
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			}
			JSONObject json = new JSONObject(requestData);
			long userId = json.getLong("u_id");
			long conId = json.getLong("f_id");
			List<Message> list = new ArrayList<>();
			list.addAll(messageService.getContactMessages(userId, conId, c.getId()));
			list.addAll(messageService.getContactMessages(conId, userId, c.getId()));
			return ResponseEntity.ok().body(list);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
		}
	}

	@PostMapping("/send-message")
	public ResponseEntity<?> saveUserMessage(String requestData, HttpSession session) {
		try {
			UserDto c = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			if (c == null) {
				return ResponseEntity.badRequest().body(AppConstant.SESSION_EXPIRED);
			}
			JSONObject json = new JSONObject(requestData);
			long userId = json.getLong("username");
			long friendId = json.getLong("friend_id");
			String message = json.getString("myMessage");
			User f = userService.getSingleUser(friendId);
			Set<Message> set = new HashSet<>();
			set.add(messageService.saveMessage(userId,
					new Message(f, message.getBytes(), "", LocalDateTime.now().toString())));
			return ResponseEntity.ok().body(set);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
		}
	}

	@RequestMapping("/doc/view/{page}")
	public String viewDocPage(@PathVariable("page") Integer page, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);
		if (!StringUtils.isEmpty(userName)) {
			UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			Page<Document> docs = docService.fetchDocumentsByStudent(user, page);

			model.addAttribute("docs", docs);
			model.addAttribute(AppConstant.CUR_PAGE, page);
			model.addAttribute(AppConstant.TOTAL_PAGES, docs.getTotalPages());

			model.addAttribute(AppConstant.TITLE, "Student - View Documents");
		}
		return "student/view_doc";
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

		return "student/create_note";
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

	@RequestMapping("/todo-list/view/{page}")
	public String viewTodoList(@PathVariable("page") Integer page, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		if (!StringUtils.isEmpty(userName)) {
			UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			Page<Todo> todos = todoService.fetchTodosByUser(page, user.getId());
			model.addAttribute("todos", todos);
			model.addAttribute(AppConstant.CUR_PAGE, page);
			model.addAttribute(AppConstant.TOTAL_PAGES, todos.getTotalPages());
			model.addAttribute(AppConstant.TITLE, "Student - Todo List");
		}
		return "student/todo_list";
	}

	@RequestMapping("/todo-list/create")
	public String createTodo(@ModelAttribute("todoDto") CreateTodoDto todoDto, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);
				todoService.createTodo(user.getId(), todoDto);
				session.setAttribute(AppConstant.TODO_MSG, new Result("Todo created", AppConstant.SUCCESS_STATUS));
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.TODO_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "redirect:/student/todo-list/view/0";
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
		return RED_TODOLIST_VIEW_URL + page;
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
		return RED_TODOLIST_VIEW_URL + page;
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
		return RED_TODOLIST_VIEW_URL + page;
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
		return RED_TODOLIST_VIEW_URL + page;
	}

	@RequestMapping("/assignment/view-titles")
	public String viewAssignments(Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		if (!StringUtils.isEmpty(userName)) {
			UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);
			List<Map<String, String>> assignments = assignmentService.fetchAssignmentTitlesByStudent(user);
			model.addAttribute("assignments", assignments);
			model.addAttribute(AppConstant.TITLE, "Student - Assessments");
		}
		return "student/view-titles";
	}

	@RequestMapping("/assignment/start/{title}")
	public String startAssignments(@PathVariable String title, Model model, HttpSession session) {
		try {
			String userName = (String) session.getAttribute(AppConstant.USER_NAME);

			if (!StringUtils.isEmpty(userName)) {
				List<Assignments> assignmentsList = assignmentService.fetchQuestionsForStudentsByTitle(title);

				UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);
				assignmentService.updateAssignmentStatusForUser(user.getId(), title);

				model.addAttribute("assignmentsList", assignmentsList);
				model.addAttribute("duration", assignmentsList.get(0).getDuration());

				session.setAttribute("title", title);
			}

			return "student/questions";

		} catch (Exception e) {
			session.setAttribute("assessment_result", new Result(e.getMessage(), AppConstant.DANGER_STATUS));
			return "redirect:/student/assignment/view-titles";
		}
	}

	@RequestMapping("/assignment/submit-answer")
	public String submitAnswer(@RequestBody List<QuestionAnswerDto> questionAnswers, Model model, HttpSession session) {
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);

		if (!StringUtils.isEmpty(userName)) {
			UserDto user = (UserDto) session.getAttribute(AppConstant.USER_DTO);

			String title = (String) session.getAttribute("title");

			assignmentService.submitAnswer(user, questionAnswers, title);
		}

		return "redirect:/student/assignment/view-titles";
	}

	@RequestMapping("/change_password")
	public String changePasswordPage(Model model) {

		model.addAttribute(AppConstant.TITLE, "Student - Update Password");
		return "student/change_password";
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
				session.setAttribute("pwd_change_msg", new Result("Password Not Matching.", AppConstant.DANGER_STATUS));
			}
		}

		return "student/change_password";
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
		return "redirect:/student/note/view/0";
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

				model.addAttribute(AppConstant.TITLE, "Student - Notes");
			}
		} catch (Exception e) {
			session.setAttribute(AppConstant.NOTE_MSG, new Result(e.getMessage(), AppConstant.DANGER_STATUS));
		}
		return "student/notes";
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
		return "redirect:/student/note/view/" + page;
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
		return "redirect:/student/note/view/" + page;
	}
}