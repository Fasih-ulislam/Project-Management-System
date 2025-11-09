
document.addEventListener("DOMContentLoaded", function () {
    const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

    fetch('http://localhost:8080/projects/my-projects', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token') // Adjust if you're storing the token elsewhere
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch projects');
        }
        return response.json();
    })
    .then(projects => {
        const projectList = document.getElementById('project-list');
        if (projects.length === 0) {
            projectList.innerHTML = '<p>No projects assigned.</p>';
            return;
        }

        projects.forEach(project => {
            const card = document.createElement('div');
            card.className = 'project-card';

            card.innerHTML = `
                <div class="project-info">
                    <h3>${project.name}</h3>
                    <p class="description">${project.description || 'No description available.'}</p>
                    <p>Start Date: ${project.startDate || 'N/A'}</p>
                    <p>Deadline: ${project.endDate || 'N/A'}</p>
                    <p>Status: ${project.status || 'Not specified'}</p>
                    <p>Budget: ${project.budget ? '$' + project.budget : 'N/A'}</p>
                    <a href="tasks.html?id=${project.id}" class="view-details-btn">View Details</a>
                </div>
            `;

            projectList.appendChild(card);
        });
    })
    .catch(error => {
        console.error('Error loading projects:', error);
        document.getElementById('project-list').innerHTML = '<p>Error loading projects. Please try again later.</p>';
    });

    
});
