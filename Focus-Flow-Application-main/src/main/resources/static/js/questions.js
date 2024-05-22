

let timerDurationSeconds = document.getElementById('assignment-duration').value * 60;

// Function to update the timer display
function updateTimerDisplay() {
	const timerElement = document.getElementById('timer');
	const minutes = Math.floor(timerDurationSeconds / 60);
	const seconds = timerDurationSeconds % 60;
	timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
}

// Function to start the timer
function startTimer() {
	updateTimerDisplay();
	const interval = setInterval(function() {
		timerDurationSeconds--;
		updateTimerDisplay();
		if (timerDurationSeconds <= 0) {
			clearInterval(interval);
			// Call your Spring Boot API endpoint here
			submitAnswer();
		}
	}, 1000); // Update the timer every second
}

let apiCalled = false;

// Function to call Spring Boot API
function submitAnswer() {
	var questions = document.querySelectorAll('.question');
	var formData = [];

	questions.forEach(function(question, index) {
		var questionText = question.querySelector('.questionContent').textContent.trim();
		var markText = question.querySelector('.markContent').textContent.trim();
		var answer;

		// Check if it's a multiple-choice question
		var multipleChoice = question.querySelector('input[type="radio"]:checked');
		if (multipleChoice) {
			answer = multipleChoice.value;
		} else if (question.querySelector('textarea[name="answer[]"]')) {
			answer = question.querySelector('textarea[name="answer[]"]').value.trim();
		} else {
			answer = "";
		}

		formData.push({
			question: questionText,
			answer: answer,
			mark: markText
		});
	});

	fetch('/student/assignment/submit-answer', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(formData)
	}).then(response => {
		if (response.ok) {
			window.location.href = '/student/assignment/view-titles';
		}
	})
	.catch(error => console.error('Error:', error));
}

// Start the timer when the page is loaded
startTimer();

document.addEventListener('contextmenu', event => event.preventDefault());

var start = function() {
	var video = document.getElementById('video'), vendorUrl = window.URL || window.webkitURL;
	if (navigator.mediaDevices.getUserMedia) {
		navigator.mediaDevices.getUserMedia({ video: true })
			.then(function(stream) {
				video.srcObject = stream;
			}).catch(function() {
				console.log("Something went wrong!");
			});
	}
}
start();

document.addEventListener('copy', function(event) {
	event.preventDefault(); // Prevent the default copy behavior
});

document.addEventListener('paste', function(event) {
	event.preventDefault(); // Prevent the default copy behavior
});

document.addEventListener('refresh', function(event) {
	event.preventDefault(); // Prevent the default copy behavior
});

window.addEventListener('beforeunload', function(event) {
    // Customize the message shown in the confirmation dialog
    event.returnValue = 'Are you sure you want to leave? Assessment will be automatically submitted.';

    // Submit the form
    submitAnswer();
});