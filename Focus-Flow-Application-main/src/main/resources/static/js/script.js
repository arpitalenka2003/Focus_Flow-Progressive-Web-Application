
const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

///TODO/////////////////////////
$('#editTodoModalLong').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget);
	var id = button.data('id');
	var title = button.data('title');
	var description = button.data('description');
	$('#todoeditid').val(id);
	$('#todoedittitle').val(title);
	$('#todoeditdesc').val(description);
});

$('#editNoteModal').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget);
	var id = button.data('id');
	var title = button.data('title');
	var content = button.data('content');
	$('#noteeditid').val(id);
	$('#noteedittitle').val(title);
	$('#noteeditcontent').val(content);
});

///Assignment//////////////////////////////////
var assignments = [];
var branch = "";
var semester = "";
var subject = "";
var duration = "";
var title = "";

function addAssignment() {

	var element = document.getElementById("save-assignment");

	// Remove the class
	element.classList.remove("invisible");

	var assignment = {
		question: "",
		answer: "",
		mark: "",
		isMcq: "",
		mcq1: "",
		mcq2: "",
		mcq3: "",
		mcq4: ""
	};

	assignments.push(assignment);

	var container = document.getElementById("assignmentsContainer");

	var assignmentDiv = document.createElement("div");
	assignmentDiv.classList.add("assignment");

	assignmentDiv.innerHTML = `
			<div class="row">
				<div class="col-md-12 mb-12">
					<div class="form-outline required">
						<label class="form-label" for="question">Question-${assignments.length}</label>
						<textarea id="add_question" data-field="question ${assignments.length}" placeholder="Question" onchange="updateQuestion(${assignments.length - 1}, this.value)" class="form-control form-control-sm add_assign_required" style="height: 100px"></textarea>
					</div>
				</div>
				<div class="col-md-12 mb-12">
					<div class="form-outline required">
						<label class="form-label" for="mark">Mark</label>
						<input type="text" id="add_mark" data-field="mark for question ${assignments.length}" placeholder="Mark" onchange="updateMark(${assignments.length - 1}, this.value)" class="form-control form-control-sm add_assign_required">
					</div>
				</div>
			</div>
			
			<label class="form-label">Multiple Choice Options:</label>
			<br>
			<div class="row mcqgroup">
				<div class="col-md-3 mb-3">
					<div class="form-outline required">
						<label class="form-label" for="mcq1${assignments.length - 1}">Option 1</label>
						<input type="text" id="mcq1${assignments.length - 1}" placeholder="MCQ 1" onchange="updateMCQ(${assignments.length - 1}, 'mcq1', this.value)" class="form-control form-control-sm add_mcq_required">
					</div>
				</div>
				<div class="col-md-3 mb-3">
					<div class="form-outline required">
						<label class="form-label" for="mcq2${assignments.length - 1}">Option 2</label>
						<input type="text" id="mcq2${assignments.length - 1}" placeholder="MCQ 2" onchange="updateMCQ(${assignments.length - 1}, 'mcq2', this.value)" class="form-control form-control-sm add_mcq_required">
					</div>
				</div>
				<div class="col-md-3 mb-3">
					<div class="form-outline required">
						<label class="form-label" for="mcq3${assignments.length - 1}">Option 3</label>
						<input type="text" id="mcq3${assignments.length - 1}" placeholder="MCQ 3" onchange="updateMCQ(${assignments.length - 1}, 'mcq3', this.value)" class="form-control form-control-sm add_mcq_required">
					</div>
				</div>
				<div class="col-md-3 mb-3">
					<div class="form-outline required">
						<label class="form-label" for="mcq4${assignments.length - 1}">Option 4</label>
						<input type="text" id="mcq4${assignments.length - 1}" placeholder="MCQ 4" onchange="updateMCQ(${assignments.length - 1}, 'mcq4', this.value)" class="form-control form-control-sm add_mcq_required">
					</div>
				</div>
			</div>
            <!-- Additional MCQ inputs can be added similarly -->

            <hr>
        `;

	container.appendChild(assignmentDiv);
}

function updateQuestion(index, value) {
	assignments[index].question = value;
}

function updateMark(index, value) {
	assignments[index].mark = value;
}

function updateMCQ(index, mcq, value) {
	assignments[index][mcq] = value;
}

function updateBranch(value) {
	branch = value;
}

function updateSemester(value) {
	semester = value;
}

function updateSubject(value) {
	subject = value;
}

function updateDuration(value) {
	duration = value;
}

function updateTitle(value) {
	title = value;
}

function validateForm() {
	// Get form inputs
	var branch = document.getElementById("add_branch").value;
	var semester = document.getElementById("add_semester").value;
	var subject = document.getElementById("add_subject").value;
	var duration = document.getElementById("add_duration").value;
	var title = document.getElementById("add_title").value;

	if (branch.trim() === "") {
		alert("Branch field is required");
		return false; // Prevent form submission
	}

	if (semester.trim() === "") {
		alert("Semester field is required");
		return false; // Prevent form submission
	}

	if (subject.trim() === "") {
		alert("Subject field is required");
		return false; // Prevent form submission
	}

	if (duration.trim() === "") {
		alert("Duration field is required");
		return false; // Prevent form submission
	}

	if (title.trim() === "") {
		alert("Title field is required");
		return false; // Prevent form submission
	}

	var elements = document.querySelectorAll('.add_assign_required');

	for (var i = 0; i < elements.length; i++) {
		var element = elements[i];
		var fieldValue = element.value.trim();
		var fieldName = element.dataset.field; // Get the value of the 'data-field' attribute

		// Check if the field value is empty
		if (fieldValue === "" || (element.tagName.toLowerCase() === "select" && fieldValue === "")) {
			alert("Please enter " + fieldName);
			return false; // Prevent form submission
		}
	}

	return validateMCQS();
	// If all validations pass, return true to allow form submission
	return true;
}

function validateMCQS() {
	var groups = document.querySelectorAll('.mcqgroup');
	var valid = true;

	groups.forEach(function(group) {
		var groupInputs = group.querySelectorAll('.add_mcq_required');
		var hasValue = false;

		// Check if any input in the group has a value
		for (var i = 0; i < groupInputs.length; i++) {
			if (groupInputs[i].value.trim() !== "") {
				hasValue = true;
				break;
			}
		}

		// If any input in the group has a value, validate all inputs in the group
		if (hasValue) {
			for (var i = 0; i < groupInputs.length; i++) {
				var fieldValue = groupInputs[i].value.trim();

				// If any input in the group is empty, set validation flag to false
				if (fieldValue === "") {
					valid = false;
					alert("Please enter other options");
					break;
				}
			}
		}
	});

	return valid;
}

function submitForm() {
	if (validateForm()) {

		var formData = {
			branch: branch,
			semester: semester,
			course: subject,
			duration: duration,
			title: title,
			assignments: assignments
		};

		var xhr = new XMLHttpRequest();
		xhr.open("POST", "/faculty/assignment/create", true);
		xhr.setRequestHeader("Content-Type", "application/json");

		xhr.onreadystatechange = function() {
			if (xhr.readyState === XMLHttpRequest.DONE) {
				if (xhr.status === 200) {
					console.log("Form submitted successfully");
					window.location.href = "/faculty/assignment/view/0";
				} else {
					console.error("Failed to submit form");
					alert(xhr.response);
				}
			}
		};

		xhr.send(JSON.stringify(formData));
	}
}

$('#editAssignModalLong').on('show.bs.modal', function(event) {
	var button = $(event.relatedTarget);
	var id = button.data('id');
	var title = button.data('title');
	var duration = button.data('duration');
	var question = button.data('question');
	var mark = button.data('mark');
	var mcq1 = button.data('mcq1');
	var mcq2 = button.data('mcq2');
	var mcq3 = button.data('mcq3');
	var mcq4 = button.data('mcq4');
	$('#assigneditid').val(id);
	$('#assignedittitle').val(title);
	$('#assigneditduration').val(duration);
	$('#assigneditquestion').val(question);
	$('#assigneditmark').val(mark);
	$('#assigneditmcq1').val(mcq1);
	$('#assigneditmcq2').val(mcq2);
	$('#assigneditmcq3').val(mcq3);
	$('#assigneditmcq4').val(mcq4);
});

$('#showAssignModalParent').on('show.bs.modal', function(event) {
	// Get the button that triggered the modal
	var button = $(event.relatedTarget);

	// Extract card title from card body
	var cardBody = button.closest('.assignment-card');
	var cardTitle = cardBody.find('.card-header').text();

	$('#showAssignModalTitle').text(cardTitle);
});

function startAssignment() {
	window.location.href = "/student/assignment/start/" + $('#showAssignModalTitle').text();
}

document.getElementById('evaluate_branch').addEventListener('change', function() {
	var value1 = this.value;
	var value2 = document.getElementById('evaluate_semester').value;
	if (value1 && value2) {
		populateStudent(value1, value2);
	}
});

document.getElementById('evaluate_semester').addEventListener('change', function() {
	var value1 = document.getElementById('evaluate_branch').value;
	var value2 = this.value;
	if (value1 && value2) {
		populateStudent(value1, value2);
	}
});

function populateStudent(value1, value2) {
	var formData = {
		branch: value1,
		semester: value2
	};

	console.log(formData);

	fetch('/faculty/student/fetch', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(formData)
	}).then(response => response.json())
		.then(data => {
			var select = document.getElementById('evaluate_students');

			// Populate the select options
			data.forEach(function(option) {
				var optionElement = document.createElement('option');
				optionElement.value = option.id;
				optionElement.textContent = option.name;
				select.appendChild(optionElement);
			});
		})
		.catch(error => console.error('Error:', error));
}

document.getElementById('evaluate_students').addEventListener('change', function() {
	var studentId = this.value;
	if (studentId) {
		populateCompletedTitle(studentId);
	}
});

function populateCompletedTitle(studentId) {

	console.log(studentId);

	fetch('/faculty/title/fetch/' + studentId, {
		method: 'GET'
	}).then(response => response.json())
		.then(data => {
			var select = document.getElementById('evaluate_title');

			// Populate the select options
			data.forEach(function(option) {
				var optionElement = document.createElement('option');
				optionElement.value = option.id;
				optionElement.textContent = option.title;
				select.appendChild(optionElement);
			});
		})
		.catch(error => console.error('Error:', error));
}

document.getElementById('evaluate_title').addEventListener('change', function() {
	var input = document.getElementById("fetch-questions");
	if (this.value !== "") {
		input.removeAttribute("disabled");
	} else {
		input.setAttribute("disabled", "disabled");
	}
});

function evaluateAnswer() {
	var questions = document.querySelectorAll('.answer-marks');
	var formData = [];

	questions.forEach(function(question, index) {
		var answerId = question.querySelector('.answer-id').textContent.trim();
		var securedMark = question.querySelector('.secured-mark').value.trim();

		formData.push({
			id: answerId,
			mark: securedMark
		});
	});

	fetch('/faculty/assignment/evaluate-answer', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(formData)
	}).then(response => {
		if (response.ok) {
			window.location.href = '/faculty/assignment/evaluate';
		}
	})
	.catch(error => console.error('Error:', error));
}