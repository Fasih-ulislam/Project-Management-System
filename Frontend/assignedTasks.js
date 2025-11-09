document.addEventListener("DOMContentLoaded", function () {
    const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

    fetch('http://localhost:8080/assigned-tasks', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token') // Adjust if you're storing the token elsewhere
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch tasks');
        }
        return response.json();
    })
    .then(projects => {
        const projectList = document.getElementById('project-list');
        if (projects.length === 0) {
            projectList.innerHTML = '<p>No tasks assigned.</p>';
            return;
        }

        projects.forEach(task => {
            const card = document.createElement('div');
            card.className = 'project-card';

            card.innerHTML = `
                <div class="project-info">
                    <h3>${task.title}</h3>
              <p>Start Date: ${task.startDate || 'N/A'}</p>
              <p>Deadline: ${task.endDate || 'N/A'}</p>
              <p>Status: ${task.completed ? "Completed" : "In Progress"}</p>
              <p class="description">${task.description}</p>
              <a href="task-details.html?taskId=${task.id}" class="view-details-btn">View Details</a>
                </div>
            `;

            projectList.appendChild(card);
        });
    })
    .catch(error => {
        console.error('Error loading tasks:', error);
        document.getElementById('project-list').innerHTML = '<p>Error loading tasks. Please try again later.</p>';
    });
});
